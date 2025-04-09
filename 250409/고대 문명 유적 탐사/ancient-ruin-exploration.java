import java.io.*;
import java.util.*;

public class Main {
	static int K, M, result;
	static int[][] map, temp;
	static Queue<Integer> wall; 
	static int[][] dir = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
	public static void main(String[] args) throws IOException {
		init();
		
		simulate();
	}
	private static void simulate() {
		while (K --> 0) {
			result = 0;
			int cnt = chooseRotate();
			if (cnt == -1) {
				return;
			}
			
			result += cnt;
			
			fill();
			
			while (true) {
				int num = getRelic();
				if (num == 0) break;
				result += num;
				fill();
			}
			
			System.out.print(result + " ");
		}	
	}
	private static void fill() {
		for (int j = 0; j < 5; j++) {
			for (int i = 4; i >= 0; i--) {
				if (!wall.isEmpty() && map[i][j] == 0) {
					map[i][j] = wall.poll();
				}
			}
		}
	}
	private static int chooseRotate() {
		temp = new int[5][5];
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				temp[i][j] = map[i][j];
			}
		}
		int max = 0;
		int bestY = 0, bestX = 0, bestR = 0;
		for (int k = 0; k < 3; k++) {
			for (int j = 0; j < 3; j++) {
				for (int i = 0; i < 3; i++) { // 0 : 90, 1 : 180, 2 : 270
					int count = rotate(i, j, k);
					if (max < count) {
						max = count;
						bestY = i;
						bestX = j;
						bestR = k;
					}
					updateMap();
				}
			}
		}
		
		if (max == 0) {
			return -1;
		}
		
		int cnt = rotate(bestY, bestX, bestR);
		
		return cnt;
	}
	private static int rotate(int y, int x, int k) {
		int[][] newMap = new int[5][5];
		for (int i = y; i < y + 3; i++) {
			for (int j = x; j < x + 3; j++) {
				int oy = i - y;
				int ox = j - x;
				
				int ry = 0, rx = 0;
				if (k == 0) {
					ry = ox;
					rx = 3 - oy - 1;
				} else if (k == 1) {
					ry = 3 - oy - 1;
					rx = 3 - ox - 1;
				} else if (k == 2) {
					ry = 3 - ox - 1;
					rx = oy;
				}
				
				newMap[y + ry][x + rx] = map[i][j];
			}
		}
		
		for (int i = y; i < y + 3; i++) {
			for (int j = x; j < x + 3; j++) {
				map[i][j] = newMap[i][j];
			}
		}
		
		return getRelic();
	}
	private static int getRelic() {
		int cnt = 0;
		boolean[][] visited = new boolean[5][5];
		boolean[][] removed = new boolean[5][5];
		
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (!visited[i][j] && map[i][j] != 0) {
					List<int[]> group = bfs(i, j, visited);
					if (group.size() >= 3) {
						cnt += group.size();
						for (int[] pos : group) {
							removed[pos[0]][pos[1]] = true;
						}
					}
				}
			}
		}
		
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (removed[i][j]) {
					map[i][j] = 0;
				}
			}
		}
		
		return cnt;
	}
	private static List<int[]> bfs(int y, int x, boolean[][] visited) {
		int num = map[y][x];
		if (num == 0) {
			return null;
		}
		List<int[]> group = new ArrayList<>();
		Queue<int[]> que = new ArrayDeque<>();
		que.offer(new int[] {y, x});
		visited[y][x] = true;
		
		while (!que.isEmpty()) {
			int[] cur = que.poll();
			group.add(cur);
			
			for (int[] d : dir) {
				int ny = cur[0] + d[0];
				int nx = cur[1] + d[1];
				
				if (ny < 0 || nx < 0 || ny >= 5 || nx >= 5 || visited[ny][nx]) continue;
				
				if (map[ny][nx] == num) {
					visited[ny][nx] = true;
					que.offer(new int[] {ny, nx});
				}
			}
		}
		
		return group;
	}
	private static void updateMap() {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				map[i][j] = temp[i][j];
			}
		}
	}
	private static void init() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		K = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		
		map = new int[5][5];
		for (int i = 0; i < 5; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < 5; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		wall = new ArrayDeque<>();
		
		st = new StringTokenizer(br.readLine());
		for (int i = 0; i < M; i++) {
			wall.offer(Integer.parseInt(st.nextToken()));
		}
	}
} 
