#!/usr/bin/env bash

sed -i "s/POSTFIX_HOSTNAME/$POSTFIX_HOSTNAME/" /etc/postfix/main.cf
sed -i "s/POSTFIX_DOMAIN/$POSTFIX_DOMAIN/" /etc/postfix/main.cf

sed -i "s/^bounce.*bounce$/bounce    unix  -       -       n       -       0       discard/" /etc/postfix/master.cf

echo "@$POSTFIX_DOMAIN    root" > /etc/postfix/virtual
echo $POSTFIX_DOMAIN > /etc/mailname
postmap /etc/postfix/virtual

postfix start-fg