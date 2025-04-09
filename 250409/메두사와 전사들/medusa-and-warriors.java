import java.io.*;
import java.util.*;

public class Main {
	static class Soldier {
		int y, x;
		boolean alive = true;
		
		public Soldier(int y, int x) {
			super();
			this.y = y;
			this.x = x;
		}
	}
	static final int INF = Integer.MAX_VALUE;
	static int N, M, startY, startX, endY, endX, totalDist, stoneCnt, attackCnt;
	static int[][] map, dist, soldierCnt, sight;
	static Soldier[] soldiers;
	static int[][] dir = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
	public static void main(String[] args) throws IOException {
		init();
		
		computeDist(endY, endX);
		
		updateSoldierCnt();
		
		simulate();
	}

	private static void simulate() {
		int cy = startY;
		int cx = startX;
		while (true) {
			// 메두사의 이동
			for (int[] d : dir) {
				int ny = cy + d[0];
				int nx = cx + d[1];
				
				if (ny < 0 || nx < 0 || ny >= N || nx >= N) continue;
				if (dist[ny][nx] < dist[cy][cx]) {
					cy = ny;
					cx = nx;
					break;
				}
			}
			
			// 공원 도착
			if (cy == endY && cx == endX) {
				System.out.println(0);
				return;
			}
			
			// 메두사의 공격
			for (int i = 0; i < M; i++) {
				if (!soldiers[i].alive) continue;
				if (cy == soldiers[i].y && cx == soldiers[i].x) {
					soldiers[i].alive = false;
				}
			}
			
			updateSoldierCnt();
			
			// 메두사의 시선
			chooseSight(cy, cx);
			
			
			// 전사들의 이동과 공격
			moveSoldier(cy, cx);
			
			updateSoldierCnt();
			
			// 출력
			System.out.println(totalDist + " " + stoneCnt + " " + attackCnt);
		}
	}
	
	private static void chooseSight(int y, int x) {
		sight = new int[N][N];
		
		stoneCnt = -1;
		int bestDir = -1;
		
		for (int d = 0; d < 4; d++) {
			
			int stone = 0;
			if (d == 0) {
				stone = sightUp(y, x);
			} else if (d == 1) {
				stone = sightDown(y, x);
			} else if (d == 2) {
				stone = sightLeft(y, x);
			} else if (d == 3) {
				stone = sightRight(y, x);
			}
			
			if (stoneCnt < stone) {
				stoneCnt = stone;
				bestDir = d;
			}
			
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					sight[i][j] = 0;
				}
			}
		}
		
		if (bestDir == 0) {
			sightUp(y, x);
		} else if (bestDir == 1) {
			sightDown(y, x);
		} else if (bestDir == 2) {
			sightLeft(y, x);
		} else if (bestDir == 3) {
			sightRight(y, x);
		}
	}

	private static int sightUp(int y, int x) {
		// 다이아몬드 모양 셀 전부
		for (int i = y - 1; i >= 0; i--) {
			int left = Math.max(0, x - (y - i));
			int right = Math.min(N - 1, x + (y - i));
			
			for (int j = left; j <= right; j++) {
				sight[i][j] = 1;
			}
		}
		
		// 장애물 처리 : 일직선
		boolean isObstruct = false;
		for (int i = y - 1; i >= 0; i--) {
			if (isObstruct) {
				sight[i][x] = 0;
			}
			
			if (soldierCnt[i][x] > 0) {
				isObstruct = true;
			}
		}
		
		// 일직선 기준 왼쪽과 오른쪽
		for (int i = y - 1; i >= 1; i--) {
			int left = Math.max(0, x - (y - i));
			int right = Math.min(N - 1, x + (y - i));
			
			// 왼쪽
			for (int j = left; j < x; j++) {
				if (sight[i][j] == 0 || soldierCnt[i][j] > 0) {
					if (j > 0) sight[i-1][j-1] = 0;
					sight[i-1][j] = 0;
				}
			}
			
			// 오른쪽
			for (int j = x + 1; j <= right; j++) {
				if (sight[i][j] == 0 || soldierCnt[i][j] > 0) {
					if (j + 1< N) sight[i-1][j+1] = 0;
					sight[i-1][j] = 0;
				}
			}
		}
		
		int stoneSoldierCnt = 0;
		
		for (int i = y - 1; i >= 0; i--) {
			int left = Math.max(0, x - (y - i));
			int right = Math.min(N - 1, x + (y - i));
			
			for (int j = left; j <= right; j++) {
				if (sight[i][j] == 1) {
					stoneSoldierCnt += soldierCnt[i][j];
				}
			}
		}
		
		return stoneSoldierCnt;
	}
	
	private static int sightDown(int y, int x) {
		// 다이아몬드 모양 셀 전부
		for (int i = y + 1; i < N; i++) {
			int left = Math.max(0, x + (y - i));
			int right = Math.min(N - 1, x - (y - i));
			
			for (int j = left; j <= right; j++) {
				sight[i][j] = 1;
			}
		}
		
		// 장애물 처리 : 일직선
		boolean isObstruct = false;
		for (int i = y + 1; i < N; i++) {
			if (isObstruct) {
				sight[i][x] = 0;
			}
			
			if (soldierCnt[i][x] > 0) {
				isObstruct = true;
			}
		}
		
		// 일직선 기준 왼쪽과 오른쪽
		for (int i = y + 1; i < N - 1; i++) {
			int left = Math.max(0, x + (y - i));
			int right = Math.min(N - 1, x - (y - i));
			
			// 왼쪽
			for (int j = left; j < x; j++) {
				if (sight[i][j] == 0 || soldierCnt[i][j] > 0) {
					if (j > 0) sight[i+1][j-1] = 0;
					sight[i+1][j] = 0;
				}
			}
			
			// 오른쪽
			for (int j = x + 1; j <= right; j++) {
				if (sight[i][j] == 0 || soldierCnt[i][j] > 0) {
					if (j + 1 < N) sight[i+1][j+1] = 0;
					sight[i+1][j] = 0;
				}
			}
		}
		
		int stoneSoldierCnt = 0;
		
		for (int i = y + 1; i < N; i++) {
			int left = Math.max(0, x + (y - i));
			int right = Math.min(N - 1, x - (y - i));
			
			for (int j = left; j <= right; j++) {
				if (sight[i][j] == 1) {
					stoneSoldierCnt += soldierCnt[i][j];
				}
			}
		}
		
		return stoneSoldierCnt;
	}
	
	private static int sightLeft(int y, int x) {
		// 다이아몬드 모양 셀 전부
		for (int j = x - 1; j >= 0; j--) {
			int top = Math.max(0, y - (x - j));
			int bot = Math.min(N - 1, y + (x - j));
			
			for (int i = top; i <= bot; i++) {
				sight[i][j] = 1;
			}
		}
		
		// 장애물 처리 : 일직선
		boolean isObstruct = false;
		for (int j = x - 1; j >= 0; j--) {
			if (isObstruct) {
				sight[y][j] = 0;
			}
			
			if (soldierCnt[y][j] > 0) {
				isObstruct = true;
			}
		}
		
		// 일직선 기준 위쪽과 아래쪽
		for (int j = x - 1; j > 0; j--) {
			int top = Math.max(0, y - (x - j));
			int bot = Math.min(N - 1, y + (x - j));
			
			// 위쪽
			for (int i = top; i < y; i++) {
				if (sight[i][j] == 0 || soldierCnt[i][j] > 0) {
					if (i > 0) sight[i-1][j-1] = 0;
					sight[i][j-1] = 0;
				}
			}
			
			// 아래쪽
			for (int i = y + 1; i <= bot; i++) {
				if (sight[i][j] == 0 || soldierCnt[i][j] > 0) {
					if (i + 1 < N) sight[i+1][j-1] = 0;
					sight[i][j-1] = 0;
				}
			}
		}
		
		int stoneSoldierCnt = 0;
		
		for (int j = x - 1; j >= 0; j--) {
			int top = Math.max(0, y - (x - j));
			int bot = Math.min(N - 1, y + (x - j));
			
			for (int i = top; i <= bot; i++) {
				if (sight[i][j] == 1) {
					stoneSoldierCnt += soldierCnt[i][j];
				}
			}
		}
		
		return stoneSoldierCnt;
	}
	
	private static int sightRight(int y, int x) {
		// 다이아몬드 모양 셀 전부
		for (int j = x + 1; j < N; j++) {
			int top = Math.max(0, y + (x - j));
			int bot = Math.min(N - 1, y - (x - j));
			
			for (int i = top; i <= bot; i++) {
				sight[i][j] = 1;
			}
		}
		
		// 장애물 처리 : 일직선
		boolean isObstruct = false;
		for (int j = x + 1; j < N; j++) {
			if (isObstruct) {
				sight[y][j] = 0;
			}
			
			if (soldierCnt[y][j] > 0) {
				isObstruct = true;
			}
		}
		
		// 일직선 기준 위쪽과 아래쪽
		for (int j = x + 1; j < N - 1; j++) {
			int top = Math.max(0, y + (x - j));
			int bot = Math.min(N - 1, y - (x - j));
			
			// 위쪽
			for (int i = top; i < y; i++) {
				if (sight[i][j] == 0 || soldierCnt[i][j] > 0) {
					if (i > 0) sight[i-1][j+1] = 0;
					sight[i][j+1] = 0;
				}
			}
			
			// 아래쪽
			for (int i = y + 1; i <= bot; i++) {
				if (sight[i][j] == 0 || soldierCnt[i][j] > 0) {
					if (i + 1 < N) sight[i+1][j+1] = 0;
					sight[i][j+1] = 0;
				}
			}
		}
		
		int stoneSoldierCnt = 0;
		
		for (int j = x + 1; j < N; j++) {
			int top = Math.max(0, y + (x - j));
			int bot = Math.min(N - 1, y - (x - j));
			
			for (int i = top; i <= bot; i++) {
				if (sight[i][j] == 1) {
					stoneSoldierCnt += soldierCnt[i][j];
				}
			}
		}
		
		return stoneSoldierCnt;
	}

	private static void moveSoldier(int y, int x) {
		totalDist = 0;
		attackCnt = 0;
		
		for (int i = 0; i < M; i++) {
			if (!soldiers[i].alive) continue;
			
			int cy = soldiers[i].y;
			int cx = soldiers[i].x;
			
			if (sight[cy][cx] == 1) continue;
			
			int curDist = calDist(new int[] {y, x}, new Soldier(cy, cx));
			boolean move = false;
			
			for (int[] d : dir) {
				int ny = cy + d[0];
				int nx = cx + d[1];
				
				if (ny < 0 || nx < 0 || ny >= N || nx >= N) continue;
				if (sight[ny][nx] == 1) continue;
				
				int newDist = calDist(new int[] {y, x}, new Soldier(ny, nx));
				if (newDist < curDist) {
					cy = ny;
					cx = nx;
					move = true;
					totalDist++;
					break;
				}
			}
			
			if (move) {
				int newDist = calDist(new int[] {y, x}, new Soldier(cy, cx));
				for (int d = 0; d < 4; d++) {
					int ny = cy + dir[(d + 2) % 4][0];
					int nx = cx + dir[(d + 2) % 4][1];
					
					if (ny < 0 || nx < 0 || ny >= N || nx >= N) continue;
					if (sight[ny][nx] == 1) continue;
					
					int newnewDist = calDist(new int[] {y, x}, new Soldier(ny, nx));
					if (newnewDist < newDist) {
						cy = ny;
						cx = nx;
						totalDist++;
						break;
					}
				}
			}
			
			soldiers[i].y = cy;
			soldiers[i].x = cx;
		}
		
		for (int i = 0; i < M; i++) {
			if (!soldiers[i].alive) continue;
			if (sight[soldiers[i].y][soldiers[i].x] == 1) continue;
			
			if (soldiers[i].y == y && soldiers[i].x == x) {
				soldiers[i].alive = false;
				attackCnt++;
			}
		}
	}
	
	private static void computeDist(int y, int x) {
		dist = new int[N][N];
		
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (map[i][j] == 1) {
					dist[i][j] = INF;
				} else {
					dist[i][j] = -1;
				}
			}
		}
		
		Queue<int[]> que = new ArrayDeque<>();
		que.offer(new int[] {y, x});
		dist[y][x] = 0;
		
		while (!que.isEmpty()) {
			int[] cur = que.poll();
			int cy = cur[0];
			int cx = cur[1];
			
			for (int[] d : dir) {
				int ny = cy + d[0];
				int nx = cx + d[1];
				
				if (ny < 0 || nx < 0 || ny >= N || nx >= N) continue;
				if (dist[ny][nx] != -1) continue;
				
				dist[ny][nx] = dist[cy][cx] + 1;
				que.offer(new int[] {ny, nx});
			}
		}
	}
	
	private static void updateSoldierCnt() {
		soldierCnt = new int[N][N];
		
		for (int i = 0; i < M; i++) {
			if (!soldiers[i].alive) continue;
			int y = soldiers[i].y;
			int x = soldiers[i].x;
			
			soldierCnt[y][x]++;
		}
	}

	private static int calDist(int[] player, Soldier soldier) {
		return Math.abs(player[0] - soldier.y) + Math.abs(player[1] - soldier.x);
	}

	private static void init() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		
		st = new StringTokenizer(br.readLine());
		startY = Integer.parseInt(st.nextToken());
		startX = Integer.parseInt(st.nextToken());
		endY = Integer.parseInt(st.nextToken());
		endX = Integer.parseInt(st.nextToken());
		
		soldiers = new Soldier[M];
		st = new StringTokenizer(br.readLine());
		for (int i = 0; i < M; i++) {
			int y = Integer.parseInt(st.nextToken());
			int x = Integer.parseInt(st.nextToken());
			soldiers[i] = new Soldier(y, x); 
		}
		
		map = new int[N][N];
		for (int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < N; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
	}
} 
