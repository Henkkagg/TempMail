import smtplib
import random
import string
import sys
from email.mime.text import MIMEText

def random_string(length=8):
    return ''.join(random.choices(string.ascii_lowercase, k=length))

if len(sys.argv) != 2:
    print("Usage: python3 emailtest.py <recipient>")
    sys.exit(1)

sender = f"{random_string()}@{random_string()}.com"
recipient = sys.argv[1]

msg = MIMEText("This is a test email body. https://example.com http://example.com")
msg['Subject'] = f"Testing äöå{random_string()}"
msg['From'] = sender
msg['To'] = recipient

with smtplib.SMTP('localhost', 25) as smtp:
    smtp.sendmail(sender, recipient, msg.as_string())

print(f"Sent from {sender} to {recipient}")