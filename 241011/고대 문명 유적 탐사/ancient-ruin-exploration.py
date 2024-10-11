from collections import deque

length = 5
dy = [-1, 0, 1, 0]
dx = [0, 1, 0, -1]

graph = []

def inRage(y, x):
    return 0 <= y < length and 0 <= x < length

def newPiece(arr):
    for i in range(length):
        for j in reversed(range(length)):
            if arr[j][i] == 0:
                if wall_num:
                    arr[j][i] = wall_num.popleft()
                else:
                    return

def getRelics(arr):
    visited = [[False] * length for _ in range(length)]
    countRelics = 0
    for i in range(length):
        for j in range(length):
            if not visited[i][j] and arr[i][j] != 0:
                queue, trace = deque([(i, j)]), deque([(i, j)])
                visited[i][j] = True
                while queue:
                    cur_y, cur_x = queue.popleft()
                    for k in range(4):
                        ny, nx = cur_y + dy[k], cur_x + dx[k]
                        if inRage(ny, nx) and arr[ny][nx] == arr[cur_y][cur_x]:
                            if not visited[ny][nx]:
                                visited[ny][nx] = True
                                trace.append((ny, nx))
                                queue.append((ny, nx))
                if len(trace) >= 3:
                    countRelics += len(trace)
                    while trace:
                        t = trace.popleft()
                        arr[t[0]][t[1]] = 0
    return countRelics

def rotate90(sy, sx, cnt, arr):
    newGraph = [row[:] for row in arr]
    tmp = [[0] * 3 for _ in range(3)]
    for _ in range(cnt):
        for y in range(3):
            for x in range(3):
                ry, rx = x, 2 - y
                tmp[ry][rx] = newGraph[sy+y][sx+x]
        for y in range(3):
            for x in range(3):
                newGraph[sy+y][sx+x] = tmp[y][x]
    return newGraph

def explore(k):
    global graph
    for _ in range(k):
        maxScore = 0
        maxScoreBoard = None
        for cnt in range(1, 4):
            for i in range(3):
                for j in range(3):
                    new_arr = [row[:] for row in graph]
                    new_arr = rotate90(i, j, cnt, new_arr)
                    result = getRelics(new_arr)
                    if maxScore < result:
                        maxScore = result
                        maxScoreBoard = new_arr
        if maxScoreBoard is None:
            break
        new_graph = maxScoreBoard
        while True:
            newPiece(new_graph)
            new_result = getRelics(new_graph)
            if new_result == 0:
                break
            maxScore += new_result
        print(maxScore, end=" ")
        graph = new_graph


k, m = map(int, input().split())
for _ in range(length):
    graph.append(list(map(int, input().split())))

wall_num = deque()
for i in list(map(int, input().split())):
    wall_num.append(i)

explore(k)