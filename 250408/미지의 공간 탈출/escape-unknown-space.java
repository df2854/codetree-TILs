import java.io.*;
import java.util.*;

public class Main {
	static class Fire {
		int sy, sx, dir, v;
		boolean alive = true;

		public Fire(int sy, int sx, int dir, int v) {
			super();
			this.sy = sy;
			this.sx = sx;
			this.dir = dir;
			this.v = v;
		}
	}
	static int N, M, F, T, mapStartY, mapStartX, endY, endX, timeWallStartY, timeWallStartX, offsetY, offsetX;
	static int[][] map, timeWallMap, upMap, eastMap, westMap, northMap, southMap;
	static int[][] dir = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
	static int[][] visited;
	static Fire[] fires;
	public static void main(String[] args) throws IOException {
		init();
		
		T = findTimeWallExit();

		if (T == -1) {
			System.out.println(T);
			return;
		}
		
		for (int i = 0; i < T; i++) {
			for (int j = 0; j < F; j++) {
				if (!fires[j].alive) continue;
				if (i % fires[j].v == 0 && i > 0) {
					int ny = fires[j].sy + dir[fires[j].dir][0];
					int nx = fires[j].sx + dir[fires[j].dir][1];
				
					if (ny < 0 || nx < 0 || ny >= N || nx >= N || map[ny][nx] == 1) {
						fires[j].alive = false;
						break;
					} 
					
					fires[j].sy = ny;
					fires[j].sx = nx;
					map[ny][nx] = -1;
				}
			}
		}
		
		int time = simulate();
		
		if (time == -1) {
			System.out.println(time);
			return;
		}
		
		System.out.println(T+1);
	}
	private static int simulate() {
		Queue<int[]> que = new ArrayDeque<>();
		visited = new int[N][N];
		que.offer(new int[] {mapStartY, mapStartX});
		visited[mapStartY][mapStartX] = 1;
		while (!que.isEmpty()) {
			
			for (int i = 0; i < F; i++) {
				if (!fires[i].alive) continue;
				if (T % fires[i].v != 0) continue;
				
				int ny = fires[i].sy + dir[fires[i].dir][0];
				int nx = fires[i].sx + dir[fires[i].dir][1];
				
				if (ny < 0 || nx < 0 || ny >= N || nx >= N || map[ny][nx] == 1) {
					fires[i].alive = false;
					continue;
				}
				
				fires[i].sy = ny;
				fires[i].sx = nx;
				map[ny][nx] = -1;
			}
			
			int size = que.size();
			for (int i = 0; i < size; i++) {
				int[] cur = que.poll();
				int cy = cur[0];
				int cx = cur[1];
				
				if (map[cy][cx] == -1) continue;
				for (int[] d : dir) {
					int ny = cy + d[0];
					int nx = cx + d[1];
					
					if (ny < 0 || nx < 0 || ny >= N || nx >= N || map[ny][nx] == 1 || map[ny][nx] == -1) continue;
					
					if (map[ny][nx] == 4 && visited[ny][nx] == 0) {
						return visited[cy][cx];
					}
					if (map[ny][nx] == 0 && visited[ny][nx] == 0) {
						visited[ny][nx] = visited[cy][cx] + 1;
						que.offer(new int[] {ny, nx});
					}
				}
			}
			
			T++;
		}
		
		return -1;
	}
	static int findTimeWallExit() {
		visited = new int[3*M][3*M];
		Queue<int[]> que = new ArrayDeque<>();
		que.offer(new int[] {timeWallStartY, timeWallStartX});
		visited[timeWallStartY][timeWallStartX] = -1;
		
		while (!que.isEmpty()) {
			int[] cur = que.poll();
			int cy = cur[0];
			int cx = cur[1];
			
			for(int[] d : dir) {
				int ny = cy + d[0];
				int nx = cx + d[1];
				
				if (ny < 0 && map[offsetY - 1][offsetX + nx - M] == 0) {
					return -visited[cy][cx];
				} else if (nx < 0 && map[offsetY + ny - M][offsetX - 1] == 0) {
					return -visited[cy][cx];
				} else if (ny >= 3*M && map[offsetY + M][offsetX + nx - M] == 0) {
					return -visited[cy][cx];
				} else if (nx >= 3*M && map[offsetY + ny - M][offsetX + M] == 0) {
					return -visited[cy][cx];
				}
				
				if (ny >= 3*M || nx >= 3*M || ny < 0 || nx < 0) continue;
				
				if (ny < M && nx >= 2*M) {
					int tempX = nx;
					ny = M - 1 - (tempX - 2*M);
					nx = 2*M - 1;
				} else if (ny >= 2*M && nx < M) {
					int tempY = ny;
					ny = 2*M - 1;
					nx = 3*M - 1 - tempY;
				} else if ((ny < M && nx < M) || (ny >= 2*M && nx >= 2*M)) {
					ny = cx;
					nx = cy;
				}
				
				if (timeWallMap[ny][nx] == 0 && visited[ny][nx] == 0) {
					visited[ny][nx] = visited[cy][cx] - 1;
					que.offer(new int[] {ny, nx});
				}
			}
		}
		
		return -1;
	}
	
	static void init() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		F = Integer.parseInt(st.nextToken());
		
		endY = -1;
		endX = -1;
		
		map = new int[N][N];
		for (int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < N; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
				if (map[i][j] == 4) {
					endY = i;
					endX = j;
				}
				if (map[i][j] == 0) {
					for (int d = 0; d < 4; d++) {
						int ny = i + dir[d][0];
						int nx = j + dir[d][1];
						
						if (ny >= 0 && nx >= 0 && ny < N && nx < N && map[ny][nx] == 3) {
							mapStartY = i;
							mapStartX = j;
						}
					}
				}
			}
		}
		
		out: for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (map[i][j] == 3) {
					offsetY = i;
					offsetX = j;
					break out;
				}
			}
		}
		
		timeWallMap = new int[3*M][3*M];
		
		eastMap = new int[M][M];
		westMap = new int[M][M];
		southMap = new int[M][M];
		northMap = new int[M][M];
		upMap = new int[M][M];
		
		for (int i = 0; i < M; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < M; j++) {
				eastMap[M-j-1][i] = Integer.parseInt(st.nextToken());
			}
		}
		for (int i = 0; i < M; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < M; j++) {
				westMap[j][M-i-1] = Integer.parseInt(st.nextToken());
			}
		}
		for (int i = 0; i < M; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < M; j++) {
				southMap[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		for (int i = 0; i < M; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < M; j++) {
				northMap[M-i-1][M-j-1] = Integer.parseInt(st.nextToken());
			}
		}
		for (int i = 0; i < M; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < M; j++) {
				upMap[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		timeWallStartY = -1;
		timeWallStartX = -1;
		
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < M; j++) {
				timeWallMap[i][j+M] = northMap[i][j];
				timeWallMap[i+M][j] = westMap[i][j];
				timeWallMap[i+M][j+M] = upMap[i][j];
				timeWallMap[i+M][j+2*M] = eastMap[i][j];
				timeWallMap[i+2*M][j+M] = southMap[i][j];
				
				if (upMap[i][j] == 2) {
					timeWallStartY = i + M;
					timeWallStartX = j + M;
				}
			}
		}
		
		fires = new Fire[F];
		for (int i = 0; i < F; i++) {
			st = new StringTokenizer(br.readLine());
			int y = Integer.parseInt(st.nextToken());
			int x = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			int v = Integer.parseInt(st.nextToken());
			
			map[y][x] = -1;
			
			fires[i] = new Fire(y, x, d, v);
		}
		
		T = 0;
	}
} 
