import os
import sys
import email
import psycopg
from email.header import decode_header

def decode_field(field):
    if field is None:
        return None
    decoded_parts = decode_header(field)
    result = ""
    for part, charset in decoded_parts:
        if isinstance(part, bytes):
            result += part.decode(charset or "utf-8")
        else:
            result += part
    return result

rawEmail = sys.stdin.read()
message = email.message_from_string(rawEmail)
recipient = decode_field(message["To"])
sender = decode_field(message["From"])
subject = decode_field(message["Subject"])

def extract_body(message):
    if message.is_multipart():
        for part in message.walk():
            content_type = part.get_content_type()
            disposition = part.get("Content-Disposition", "")
            if content_type == "text/plain" and "attachment" not in disposition:
                return part.get_payload(decode=True).decode(part.get_content_charset() or "utf-8")
    else:
        return message.get_payload(decode=True).decode(message.get_content_charset() or "utf-8").rstrip("\n")

body = extract_body(message)
connection = psycopg.connect(
    host="postgres",
    dbname="postgres",
    user="postgres",
    password="postgres"
)
cursor = connection.cursor()
cursor.execute(
    "INSERT INTO mail (recipient, sender, subject, body) VALUES (%s, %s, %s, %s)",
    (recipient, sender, subject, body)
)
connection.commit()