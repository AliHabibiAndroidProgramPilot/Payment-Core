#!/usr/bin/env python3
"""
Mock TCP payment server for payment_core.

Framing: newline-delimited JSON. One request -> one response per connection,
matching the assessment's connect -> send -> receive -> close flow.

Run:
    python3 mock_tcp_server.py [port]

Default port: 9999
"""

import json
import socket
import threading
import time
import sys
from typing import Optional

HOST = "0.0.0.0"
PORT = int(sys.argv[1]) if len(sys.argv) > 1 else 9999

# --- Hardcoded scenarios, keyed by requestId prefix -------------------------
# These exist to exercise the TcpClient's error handling paths from the spec:
# connect timeout, read timeout, invalid response handling, safe socket closing.
#
#   REQ-TIMEOUT-*   never responds (holds connection open)  -> read timeout
#   REQ-SLOW-*      responds after a 5s delay                -> tune vs. your read timeout
#   REQ-DECLINE-*   responds with a business decline (05)    -> normal failure path
#   REQ-MALFORMED-* responds with invalid JSON                -> response parsing failure
#   REQ-DROP-*      closes the socket with no response        -> abrupt disconnect handling
#   anything else   responds with an approval (00)

def build_response(request: dict) -> Optional[str]:
    request_id = request.get("requestId", "")

    if request_id.startswith("REQ-SLOW"):
        time.sleep(5)
        return json.dumps({
            "requestId": request_id,
            "responseCode": "00",
            "rrn": "123456789012",
            "message": "Approved (slow)"
        })

    if request_id.startswith("REQ-DECLINE"):
        return json.dumps({
            "requestId": request_id,
            "responseCode": "05",
            "rrn": None,
            "message": "Declined test"
        })

    if request_id.startswith("REQ-MALFORMED"):
        return "{not valid json!!"

    return json.dumps({
        "requestId": request_id,
        "responseCode": "00",
        "rrn": "123456789012",
        "message": "Successfully one"
    })


def handle_client(conn: socket.socket, addr):
    try:
        conn.settimeout(60)
        buffer = b""
        while b"\n" not in buffer:
            chunk = conn.recv(4096)
            if not chunk:
                print(f"[{addr}] client disconnected before sending a full line")
                return
            buffer += chunk

        line, _, _rest = buffer.partition(b"\n")
        try:
            request = json.loads(line.decode("utf-8"))
        except json.JSONDecodeError:
            print(f"[{addr}] received invalid JSON: {line!r}")
            return

        print(f"[{addr}] request: {request}")
        request_id = request.get("requestId", "")

        if request_id.startswith("REQ-DROP"):
            print(f"[{addr}] simulating abrupt drop for {request_id}")
            return  # close without responding

        if request_id.startswith("REQ-TIMEOUT"):
            print(f"[{addr}] simulating hang for {request_id}")
            time.sleep(3600)
            return

        response = build_response(request)
        if response is None:
            return

        print(f"[{addr}] response: {response}")
        conn.sendall((response + "\n").encode("utf-8"))

    except socket.timeout:
        print(f"[{addr}] server-side timeout waiting on client")
    except Exception as e:
        print(f"[{addr}] error: {e}")
    finally:
        conn.close()


def main():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server.bind((HOST, PORT))
    server.listen(5)
    print(f"Mock TCP payment server listening on {HOST}:{PORT}")
    print("Scenarios: REQ-TIMEOUT-*, REQ-SLOW-*, REQ-DECLINE-*, REQ-MALFORMED-*, REQ-DROP-*, else approved")

    try:
        while True:
            conn, addr = server.accept()
            threading.Thread(target=handle_client, args=(conn, addr), daemon=True).start()
    except KeyboardInterrupt:
        print("\nShutting down.")
    finally:
        server.close()


if __name__ == "__main__":
    main()
