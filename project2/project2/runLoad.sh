#!/bin/sh

# Drop all the tables from the CS144 database
mysql CS144 < drop.sql

# Create the tables in the CS144 database
mysql CS144 < create.sql

# Compile the code
ant

# Run the script on all the data xml
ant run-all

sort -u Category.dat.tmp > Category.dat

# Load all the data into the database
mysql CS144 < load.sql

# Remove the temporary data files
rm *.dat
rm *.tmp
