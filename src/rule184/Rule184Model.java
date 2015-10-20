package rule184;

import java.util.Random;

public class Rule184Model {
	private int L;	// 道路長
	private int N;	// 車の台数

	/*
	 * site[i] where 0 <= x <= L+1
	 *
	 * site[0]    = site[L] (周期的境界条件)
	 * site[1～L] : body
	 * site[L+1]  = site[0] (周期的境界条件)
	 */
	private boolean[] site1, site2;		// buffer
	private boolean[] site, site_new;	// accessor

	// バッファ切り替え用フラグ
	private boolean flagSwap = false;


	/**
	 * コンストラクタ
	 * @param L 道路長
	 * @param N 車の台数
	 */
	public Rule184Model(int L, int N) {
		this.L = L;
		this.N = N;

		// 配列の初期化
		site1 = new boolean[L + 2];
		site2 = new boolean[L + 2];
		swapBuffer();

		// 初期条件の設定
		initialize();
	}


	/**
	 * リングバッファを切り替えます。
	 */
	private void swapBuffer() {
		if (flagSwap) {
			site = site1;
			site_new = site2;
		} else {
			site = site2;
			site_new = site1;
		}
		flagSwap = !flagSwap;
	}


	/**
	 * 初期条件を設定します。
	 */
	public void initialize() {
		Random r = new Random();

		int n = 0;
		while(n != N) {
			int i = r.nextInt(L) + 1;
			if (!site[i]) {
				site[i] = true;
				n++;
			}
		}

		site[0] = site[L];
		site[L + 1] = site[1];
	}


	/**
	 * 1ステップの時間発展を行ないます。
	 * @return 速度を表す実数値
	 */
	public double update() {
		int velocity = 0;
		for (int i = 1; i <= L; i++) {
			boolean p = site[i - 1];
			boolean q = site[i];
			boolean r = site[i + 1];
			// S. Wolfram's Cellular Automaton: rule 184
			site_new[i] = (p & !q) | (q & r);

			// siteの値が異なっていたら車が動いた
			if (site[i] != site_new[i])
				velocity++;
		}

		site_new[0] = site_new[L];
		site_new[L + 1] = site_new[1];

		swapBuffer();

		return (double)(velocity / 2) / N;
	}


	/**
	 * nステップの時間発展を行ないます。
	 * @param n 時間発展させるステップ数
	 * @return 最後の時間発展における速度
	 */
	public double update(int n) {
		double v = 0.0;
		for (int i = 0; i < n; i++)
			v = update();

		return v;
	}


	/**
	 * 現在のサイトの状況を1行の文字列として出力します。
	 * 車がいるサイトは"*"で、車がいないサイトは" "で表現されます。
	 */
	@Override
	public String toString() {
		StringBuffer string = new StringBuffer(L);
		for (int i = 1; i <= L; i++) {
			string.append(site[i] ? '*' : ' ');
		}
		return string.toString();
	}


	// test
	public static void main(String[] args) {
		Rule184Model x = new Rule184Model(100, 30);
		double v;

		System.out.println(x);
		for (int i = 0; i < 50; i++) {
			v = x.update();
			System.out.println(x + "    v = " + v);
		}
	}

}
