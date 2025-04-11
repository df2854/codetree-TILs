import java.io.*;
import java.util.*;

public class Main {
	static class Point {
		int y, x;

		public Point(int y, int x) {
			super();
			this.y = y;
			this.x = x;
		}
		
		boolean isSame(Point p) {
			return this.y == p.y && this.x == p.x;
		}
	}
	static int N, M, time;
	static int[][] map, dist;
	static boolean[][] visited;
	static Point[] csList, people;
	static int[][] dir = {{-1, 0}, {0, -1}, {0, 1}, {1, 0}};
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		
		map = new int[N+1][N+1];
		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= N; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		csList = new Point[M+1];
		for (int i = 1; i <= M; i++) {
			st = new StringTokenizer(br.readLine());
			int y = Integer.parseInt(st.nextToken());
			int x = Integer.parseInt(st.nextToken());
			csList[i] = new Point(y, x);
		}
		
		people = new Point[M+1];
		for (int i = 1; i <= M; i++) {
			people[i] = new Point(-1, -1);
		}
		
		time = 0;
		while (true) {
			time++;
			simulate();

			if (end()) break;
		}
		
		System.out.println(time);
	}
	private static void simulate() {
		for (int i = 1; i <= M; i++) {
			if (people[i].isSame(new Point(-1, -1)) || people[i].isSame(csList[i])) continue;
			
			bfs(csList[i]);
			
			int cy = people[i].y;
			int cx = people[i].x;
			
			int min = Integer.MAX_VALUE;
			int y = -1, x = -1;
			for (int[] d : dir) {
				int ny = cy + d[0];
				int nx = cx + d[1];
				
				if (ny < 1 || nx < 1 || ny > N || nx > N || map[ny][nx] == -1) continue;
				if (visited[ny][nx] && min > dist[ny][nx]) {
					min = dist[ny][nx];
					y = ny;
					x = nx;
				}
			}
			
			people[i] = new Point(y, x);
		}
		
		for (int i = 1; i <= M; i++) {
			if (people[i].isSame(csList[i])) {
				int cy = people[i].y;
				int cx = people[i].x;
				map[cy][cx] = -1;
			}
		}
		
		if (time > M) return;
		
		bfs(csList[time]);
		
		int min = Integer.MAX_VALUE;
		int a = -1, b = -1;
		
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= N; j++) {
				if (map[i][j] == 1 && dist[i][j] != 0) {
					if (min > dist[i][j]) {
						min = dist[i][j];
						a = i;
						b = j;
					}
				}
			}
		}
		
		people[time] = new Point(a, b);
		map[a][b] = -1;
	}
	
	 private static void bfs(Point p) {
		dist = new int[N+1][N+1];
		int y = p.y;
		int x = p.x;
		Queue<int[]> que = new ArrayDeque<>();
		visited = new boolean[N+1][N+1];
		que.offer(new int[] {y, x});
		visited[y][x] = true;
		dist[y][x] = 0;
		
		while(!que.isEmpty()) {
			int[] cur = que.poll();
			int cy = cur[0];
			int cx = cur[1];
			
			for (int[] d : dir) {
				int ny = cy + d[0];
				int nx = cx + d[1];
				
				if (ny < 1 || nx < 1 || ny > N || nx > N || visited[ny][nx]) continue;
				if (map[ny][nx] == -1) continue;
				
				dist[ny][nx] = dist[cy][cx] + 1;
				visited[ny][nx] = true;
				que.offer(new int[] {ny, nx});
			}
		}
	}
	 
	 private static boolean end() {
		for (int i = 1; i <= M; i++) {
			if (!people[i].isSame(csList[i])) return false;
		}
		return true;
	}
} 
