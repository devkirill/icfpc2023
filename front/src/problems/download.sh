#!/bin/bash
for i in {55..56}
do
    curl "https://cdn.icfpcontest.com/problems/${i}.json" --output "${i}.json"
done