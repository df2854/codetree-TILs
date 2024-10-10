from collections import deque

MAX_L = 70
    
graph = [[0] * MAX_L for _ in range(MAX_L+3)]
dx = [-1, 0, 1, 0]
dy = [0, 1, 0, -1]
isExit = [[False] * MAX_L for _ in range(MAX_L+3)]
answer = 0

def inRange(x, y):
    return 3 <= x < r + 3 and 0 <= y < c

def reset():
    for i in range(r+3):
        for j in range(c):
            graph[i][j] = 0
            isExit[i][j] = False

def canGo(x, y):
    flag = 0 <= y - 1 and y + 1 < c and x + 1 < r + 3
    flag = flag and (graph[x-1][y-1] == 0)
    flag = flag and (graph[x-1][y] == 0)
    flag = flag and (graph[x-1][y+1] == 0)
    flag = flag and (graph[x][y-1] == 0)
    flag = flag and (graph[x][y] == 0)
    flag = flag and (graph[x][y+1] == 0)
    flag = flag and (graph[x+1][y] == 0)
    return flag

def bfs(x, y):
    result = x
    queue = deque([(x, y)])
    visited = [[False] * c for _ in range(r+3)]
    visited[x][y] = True
    while queue:
        cur_x, cur_y = queue.popleft()
        for dst in range(4):
            nx, ny = cur_x + dx[dst], cur_y + dy[dst]
            if inRange(nx, ny) and not visited[nx][ny] and (graph[nx][ny] == graph[cur_x][cur_y] or (graph[nx][ny] != 0 and isExit[cur_x][cur_y])):
                queue.append((nx, ny))
                visited[nx][ny] = True
                result = max(result, nx)
    return result
    
def gravity(x, y, d, id):
    if canGo(x+1, y):
        gravity(x+1, y, d, id)
    elif canGo(x+1, y-1):
        gravity(x+1, y-1, (d+3)%4, id)
    elif canGo(x+1, y+1):
        gravity(x+1, y+1, (d+1)%4, id)
    else:
        if not inRange(x-1, y-1) or not inRange(x+1, y+1):
            reset()
        else:
            graph[x][y] = id
            for i in range(4):
                graph[x+dx[i]][y+dy[i]] = id
            isExit[x+dx[d]][y+dy[d]] = True
            global answer
            answer += bfs(x, y) - 3 + 1

r, c, k = map(int, input().split())
for i in range(1, k+1):
    x, d = map(int, input().split())
    gravity(0, x - 1, d, i)
print(answer)