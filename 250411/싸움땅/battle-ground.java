import java.io.*;
import java.util.*;

public class Main {
	static class Person {
		int y, x, dir, initAbility, gun;

		public Person(int y, int x, int dir, int initAbility, int gun) {
			super();
			this.y = y;
			this.x = x;
			this.dir = dir;
			this.initAbility = initAbility;
			this.gun = gun;
		}
	}
	static int N, M, K;
	static List<Integer>[][] board; 
	static int[][] idBoard;
	static Person[] people;
	static int[] scores;
	static boolean[][] isPerson;
	static int[][] dir = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		
		board = new ArrayList[N][N];
		for (int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < N; j++) {
				board[i][j] = new ArrayList<>();
				int num = Integer.parseInt(st.nextToken());
				if (num != 0) {
					board[i][j].add(num);
				}
			}
		}
		
		people = new Person[M+1];
		for (int i = 1; i <= M; i++) {
			st = new StringTokenizer(br.readLine());
			int y = Integer.parseInt(st.nextToken());
			int x = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			int s = Integer.parseInt(st.nextToken());
			
			people[i] = new Person(y-1, x-1, d, s, 0);
		}
		
		scores = new int[M+1];
		for (int i = 0; i < K; i++) {
			isPerson = new boolean[N+1][N+1];
			idBoard = new int[N+1][N+1];
			simulate();
		}
		
		for (int i = 1; i <= M; i++) {
			System.out.print(scores[i] + " ");
		}
	}
	private static void simulate() {
		for (int i = 1; i <= M; i++) {
			Person cur = people[i];
			
			int y = cur.y;
			int x = cur.x;
			int d = cur.dir;
			int s = cur.initAbility;
			int a = cur.gun;
			
			int ny = y + dir[d][0];
			int nx = x + dir[d][1];
			
			if (ny < 0 || nx < 0 || ny >= N || nx >= N) {
				d = (d + 2) % 4;
				ny = y + dir[d][0];
				nx = x + dir[d][1];
			}
			
			if (isPerson[ny][nx]) {
				Person enemy = people[idBoard[ny][nx]];
				if ((s + a) > (enemy.initAbility + enemy.gun) || (s + a == (enemy.initAbility + enemy.gun) && s > enemy.initAbility)) {
					scores[i] += (s + a) - (enemy.initAbility + enemy.gun);
					move(i ,cur, ny, nx, d, s, a);
					loseMove(idBoard[ny][nx], enemy, ny, nx, enemy.dir, enemy.initAbility, enemy.gun);
				} else {
					scores[idBoard[ny][nx]] += (enemy.initAbility + enemy.gun) - (cur.initAbility + cur.gun);
					move(idBoard[ny][nx], enemy, ny, nx, enemy.dir, enemy.initAbility, enemy.gun);
					loseMove(i ,cur, ny, nx, d, s, a);
				}
			} else {
				move(i ,cur, ny, nx, d, s, a);
			}
		}
	}
	private static void move(int id, Person p, int y, int x, int d, int s, int a) {
		idBoard[y][x] = id;
		isPerson[y][x] = true;
		board[y][x].add(p.gun);
		Collections.sort(board[y][x]);
		a = board[y][x].get(board[y][x].size() - 1);
		board[y][x].remove(board[y][x].size() - 1);
		
		people[id] = new Person(y, x, d, s, a);
	}
	private static void loseMove(int id, Person p, int y, int x, int d, int s, int a) {
		board[y][x].add(a);
		
		for (int i = 0; i < 4; i++) {
			int ndir = (d + i) % 4;
			int ny = y + dir[ndir][0];
			int nx = x + dir[ndir][1];
			if (ny < 0 || nx < 0 || ny >= N || nx >= N) continue;
			
			if (isPerson[ny][nx] == false && idBoard[ny][nx] == 0) {
				people[id] = new Person(ny, nx, ndir, s, 0);
				move(id, people[id], ny, nx, ndir, s, 0);
				break;
			}
		}
	}
} 
