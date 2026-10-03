#!/usr/bin/env python3
"""Sets the passphrase that opens the debug screens.

Asks for the new passphrase (or generates one with --generate), then writes only a random salt
and a PBKDF2-SHA256 hash of it to DebugPassphrase.kt. The passphrase itself is never stored in
the repo or the app; keep it somewhere safe, e.g. a password manager.

    python3 scripts/set-debug-passphrase.py
    python3 scripts/set-debug-passphrase.py --generate
"""
import getpass
import hashlib
import os
import secrets
import string
import sys

ITERATIONS = 100_000
TARGET = os.path.join(
    os.path.dirname(__file__), "..", "app", "src", "commonMain", "kotlin",
    "com", "top10", "products", "data", "lock", "DebugPassphrase.kt",
)


def generate() -> str:
    """Four groups of five lowercase letters and digits, about 100 bits."""
    alphabet = string.ascii_lowercase + string.digits
    return "-".join("".join(secrets.choice(alphabet) for _ in range(5)) for _ in range(4))


def main() -> None:
    if "--generate" in sys.argv:
        passphrase = generate()
        print(f"New passphrase: {passphrase}")
    else:
        passphrase = getpass.getpass("New passphrase: ")
        if passphrase != getpass.getpass("Again: "):
            sys.exit("The two entries differ.")
        if len(passphrase) < 12:
            sys.exit("Use at least 12 characters.")
    salt = secrets.token_bytes(16)
    digest = hashlib.pbkdf2_hmac("sha256", passphrase.encode(), salt, ITERATIONS, 32)
    with open(TARGET, "w") as out:
        out.write(f'''package com.top10.products.data.lock

/**
 * The debug passphrase, hashed. Written by `scripts/set-debug-passphrase.py`; run it to change the
 * passphrase instead of editing this file. Only a salt and a PBKDF2-SHA256 hash are kept, so the
 * passphrase cannot be read back from the source or the app.
 */
internal object DebugPassphrase {{
    const val ITERATIONS = {ITERATIONS}
    const val SALT_HEX = "{salt.hex()}"
    const val HASH_HEX = "{digest.hex()}"
}}
''')
    print(f"Wrote {os.path.normpath(TARGET)}")


if __name__ == "__main__":
    main()
