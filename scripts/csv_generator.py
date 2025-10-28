#!/usr/bin/env python3
import csv, uuid, random, time, argparse

ENUMS = ['queries', 'time-spent-mins', 'correct-ans', 'wrong-ans']
RANGES = {
    'queries': (1, 100),
    'time-spent-mins': (1, 10),
    'correct-ans': (0, 100),
    'wrong-ans': (0, 100),
}

def main():
    p = argparse.ArgumentParser(description="Generate random CSV: uuid,epoch,enum,integer")
    p.add_argument('-n', '--rows', type=int, default=100, help='Number of rows')
    p.add_argument('-o', '--output', default='data.csv', help='Output CSV path')
    p.add_argument('--no-header', action='store_true', help='Do not write header')
    p.add_argument('--epoch-days', type=int, default=30,
                   help='Pick epoch randomly from last N days (default 30)')
    args = p.parse_args()

    now = int(time.time())
    start = now - args.epoch_days * 86400

    with open(args.output, 'w', newline='') as f:
        w = csv.writer(f)
        if not args.no_header:
            w.writerow(['uuid', 'epoch', 'enum', 'integer'])
        for _ in range(args.rows):
            e = random.choice(ENUMS)
            lo, hi = RANGES[e]
            w.writerow([
                str(uuid.uuid4()),
                random.randint(start, now),  # epoch
                e,
                random.randint(lo, hi)       # integer per enum rules
            ])

if __name__ == "__main__":
    main()

