#!/usr/bin/env bash

pg_restore -U postgres -d dvdrental /tmp/dvdrental.tar
