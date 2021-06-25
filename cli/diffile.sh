#!/bin/sh

# get changed files which different with origin branch
git diff --name-only origin/$(git branch --show-current) | cat
