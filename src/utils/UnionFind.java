package utils;

public class UnionFind {
	private int id[];
	private int sz[];
	private int quantidadeGrupos;

	public UnionFind(int size) {
		id = new int[size];
		sz = new int[size];
		this.quantidadeGrupos = size;
		for (int i = 0; i < size; i++) {
			id[i] = i;
			sz[i] = 1;
		}
	}

	public int find(int x) {
		if (x == id[x])
			return x;
		return id[x] = find(id[x]);
	}

	public void union(int x, int y) {
		int rx = find(x), ry = find(y);

		if (rx == ry)
			return;

		quantidadeGrupos--;

		sz[ry] = sz[rx] += sz[ry];

		id[ry] = id[rx];

	}

	public int quantidadeGrupos() {
		return quantidadeGrupos;
	}
}
