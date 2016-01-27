import random, sys
lines = open(sys.argv[1]).readlines()
sys.stdout.write(lines[random.randrange(len(lines))])
