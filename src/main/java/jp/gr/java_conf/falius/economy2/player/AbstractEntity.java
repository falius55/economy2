package jp.gr.java_conf.falius.economy2.player;

import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.falius.economy2.account.DebtMediator;
import jp.gr.java_conf.falius.economy2.market.Market;

public abstract class AbstractEntity implements Entity {
    private List<DebtMediator> mDebtList; // 借金のリスト
    private List<DebtMediator> mClaimList; // 貸金のリスト

    public AbstractEntity() {
        mDebtList = new ArrayList<DebtMediator>();
        mClaimList = new ArrayList<DebtMediator>();
    }

    /**
     * 借金をするため、申し込むために使うDebtMediatorオブジェクトを作成する
     * 政府から実行された場合は公債を発行します
     * 銀行から実行された場合は、コールナイトオーバー物で一時的な借受を申し込みます
     * 借金が不成立の場合は想定外
     * {@code
     *  DebtMediator debt = subject.offerDebt(100000);
     *  subject.acceptDebt(debt);
     *  }
     */
    protected final DebtMediator offerDebt(int amount) {
        DebtMediator debt = new DebtMediator(accountBook(), amount);
        mDebtList.add(debt);
        return debt;
    }

    /**
     * 借金の申し込むを受け入れ、お金を貸します
     * @return 貸した金額
     */
    protected final int acceptDebt(DebtMediator debt) {
        mClaimList.add(debt);
        debt.accepted(accountBook(), Market.INSTANCE.nowDate());
        return debt.amount();
    }

    /**
     * 借金を返済します
     */
    public void repay(int amount) {
        accountBook().repay(amount);
    }

    /**
     * 返済を受けます
     */
    @Override
    public final void repaid(int amount) {
        accountBook().repaid(amount);
    }
}
