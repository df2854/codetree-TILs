import java.io.*;
import java.util.*;

public class Main {
	static class Knight {
		int r, c, h, w, k;

		public Knight(int r, int c, int h, int w, int k) {
			super();
			this.r = r;
			this.c = c;
			this.h = h;
			this.w = w;
			this.k = k;
		}
	}
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	static StringTokenizer st;
	static int L, N, Q, moveDir, result;
	static int[][] map, knightMap;
	static int[] dmg;
	static List<Integer> moveList;
	static Map<Integer, Knight> knightList;
	static int[][] dir = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
	public static void main(String[] args) throws IOException {
		init();
		
		
		simulate();
	
		
		result = 0;
		for (int i = 1; i <= N; i++) {
			Knight k = knightList.get(i);
			if (k.k > 0) {
				result += dmg[i];
			}
		}
		System.out.println(result);
	}
	
	private static void simulate() throws IOException {
		for (int t = 0; t < Q; t++) {
			st = new StringTokenizer(br.readLine());
			int knightID = Integer.parseInt(st.nextToken());
			int knightDir = Integer.parseInt(st.nextToken());
			
			moveList = new ArrayList<>();
			
			if (isMove(knightID, knightDir)) {
				for (int moveId : moveList) {
					Knight k = knightList.get(moveId);
					for (int i = k.r; i < k.r + k.h; i++) {
						for (int j = k.c; j < k.c + k.w; j++) {
							knightMap[i][j] = 0;
						}
					}
				}
				
				for (int moveId : moveList) {
					Knight k = knightList.get(moveId);
					k.r += dir[moveDir][0];
					k.c += dir[moveDir][1];
				}
				
				for (int moveId : moveList) {
					Knight k = knightList.get(moveId);
					for (int i = k.r; i < k.r + k.h; i++) {
						for (int j = k.c; j < k.c + k.w; j++) {
							knightMap[i][j] = moveId;
						}
					}
				}
				
				for (int moveId : moveList) {
					if (moveId == knightID) continue;
					Knight k = knightList.get(moveId);
					int damage = 0;
					for (int i = k.r; i < k.r + k.h; i++) {
						for (int j = k.c; j < k.c + k.w; j++) {
							if (map[i][j] == 1) damage++;
						}
					}
					k.k -= damage;
					dmg[moveId] += damage;
					
					if (k.k <= 0) {
						// 사망 → 맵에서 제거
						for (int i = k.r; i < k.r + k.h; i++) {
							for (int j = k.c; j < k.c + k.w; j++) {
								knightMap[i][j] = 0;
							}
						}
					}
				}
			}
		}
	}


	private static boolean isMove(int id, int d) {
		Knight knight = knightList.get(id);
		if (knight.k <= 0) return false;
		
		List<Integer> moveGroup = new ArrayList<>();
		moveGroup.add(id);
		Queue<Integer> que = new ArrayDeque<>();
		que.offer(id);
		
		while(!que.isEmpty()) {
			int cur = que.poll();
			Knight curKnight = knightList.get(cur);
			int nr = curKnight.r + dir[d][0];
			int nc = curKnight.c + dir[d][1];
			
			if (nr < 1 || nc < 1 || nr + curKnight.h - 1 > L || nc + curKnight.w - 1 > L) return false;
			for (int i = nr; i < nr + curKnight.h; i++) {
				for (int j = nc; j < nc + curKnight.w; j++) {
					if (map[i][j] == 2) return false;
				}
			}
			
			for (int i = nr; i < nr + curKnight.h; i++) {
				for (int j = nc; j < nc + curKnight.w; j++) {
					int otherID = knightMap[i][j];
					if (otherID != 0 && knightList.get(otherID).k > 0) {
						if (!moveGroup.contains(otherID)) {
							moveGroup.add(otherID);
							que.offer(otherID);
						}
					}
				}
			}
		}
		
		moveList = new ArrayList<>(moveGroup);
		moveDir = d;
		return true;
	}

	private static void init() throws IOException {
		st = new StringTokenizer(br.readLine());
		L = Integer.parseInt(st.nextToken());
		N = Integer.parseInt(st.nextToken());
		Q = Integer.parseInt(st.nextToken());
		
		map = new int[L+1][L+1];
		for (int i = 1; i <= L; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= L; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		knightMap = new int[L+1][L+1];
		knightList = new HashMap<>();
		for (int id = 1; id <= N; id++) {
			st = new StringTokenizer(br.readLine());
			int r = Integer.parseInt(st.nextToken());
			int c = Integer.parseInt(st.nextToken());
			int h = Integer.parseInt(st.nextToken());
			int w = Integer.parseInt(st.nextToken());
			int k = Integer.parseInt(st.nextToken());
			
			for (int i = r; i < r + h; i++) {
				for (int j = c; j < c + w; j++) {
					knightMap[i][j] = id;
				}
			}
			knightList.put(id, new Knight(r, c, h, w, k));
		}
		
		dmg = new int[N+1];
	}
} 
