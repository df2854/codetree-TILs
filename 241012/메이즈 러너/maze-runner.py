from collections import deque

n, m, k = map(int, input().split())

board = [[0] * (n+1) for _ in range(n+1)]
for i in range(1, n+1):
    board[i] = [0] + list(map(int, input().split()))
tmp_board = [[0] * (n+1) for _ in range(n+1)]

people = [(-1, -1)] + [tuple(map(int, input().split())) for _ in range(m)]
exits = tuple(map(int, input().split()))

result = 0
sy, sx, size = 0, 0, 0

def moving():
    global result, exits
    for i in range(1, m+1):
        if people[i] == exits:
            continue
        
        ey, ex = exits
        py, px = people[i]
        
        if ey != py:
            ny, nx = py, px
            if ey > ny:
                ny += 1
            else:
                ny -= 1
            if not board[ny][nx]:
                people[i] = (ny, nx)
                result += 1
                continue
        
        if ex != px:
            ny, nx = py, px
            if ex > nx:
                nx += 1
            else:
                nx -= 1
            if not board[ny][nx]:
                people[i] = (ny, nx)
                result += 1
                continue
            
def find_square():
    global sy, sx, size, exits
    
    ey, ex = exits
    
    for sz in range(2, n+1):
        for y in range(1, n-sz+2):
            for x in range(1, n-sz+2):
                y2, x2 = y + sz - 1, x + sz - 1
                
                if not (y <= ey <= y2 and x <= ex <= x2):
                    continue
                
                is_human_in = False
                for i in range(1, m+1):
                    py, px = people[i]
                    if y <= py <= y2 and x <= px <= x2:
                        if not(py == ey and px == ex):
                            is_human_in = True
                            
                if is_human_in:
                    sy, sx = y, x
                    size = sz
                    return

def rotate_square():
    for y in range(sy, sy + size):
        for x in range(sx, sx + size):
            if board[y][x]:
                board[y][x] -= 1
                
    for y in range(sy, sy + size):
        for x in range(sx, sx + size):
            oy, ox = y - sy, x - sx
            ry, rx = ox, size - oy - 1
            
            tmp_board[sy+ry][sx+rx] = board[y][x]
            
    for y in range(sy, sy + size):
        for x in range(sx, sx + size):
            board[y][x] = tmp_board[y][x]
            
def rotate_peaple_exit():
    global exits
    
    for i in range(1, m+1):
        py, px = people[i]
        
        if sy <= py < sy+size and sx <= px < sx+size:
            oy, ox = py - sy, px - sx
            ry, rx = ox, size - oy - 1
            
            people[i] = (sy+ry, sx+rx)
            
    ey, ex = exits
    
    if sy <= ey < sy+size and sx <= ex < sx+size:
        oy, ox = ey - sy, ex - sx
        ry, rx = ox, size - oy - 1
        
        exits = (sy+ry, sx+rx)
        
for _ in range(k):
    moving()
    
    is_exit = True
    for i in range(1, m+1):
        if exits != people[i]:
            is_exit = False
    
    if is_exit:
        break
    
    find_square()
    rotate_square()
    rotate_peaple_exit()
    
print(result)
    
ey, ex = exits
print(ey, ex)