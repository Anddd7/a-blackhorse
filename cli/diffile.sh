#!/bin/sh

# get changed files which different with upstream branch
git diff --name-only $(git rev-parse --symbolic-full-name --abbrev-ref @{u}) | cat
