package jp.gr.java_conf.falius.economy2.player;

import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.falius.economy2.account.Account;
import jp.gr.java_conf.falius.economy2.account.DebtMediator;
import jp.gr.java_conf.falius.economy2.market.MarketInfomation;

public abstract class AbstractEntity implements Entity {
    private Bank mainBank;

    private List<DebtMediator> mDebtList; // 借金のリスト
    private List<DebtMediator> mClaimList; // 貸金のリスト

    AbstractEntity() {
        mDebtList = new ArrayList<DebtMediator>();
        mClaimList = new ArrayList<DebtMediator>();
    }

    protected abstract Account<? extends Enum<?>> account();

    /**
     * 貯金します
     * 対象はメインバンク
     * 銀行が実行すると中央銀行に預けます
     * 中央銀行が実行すると、お金が市場から消えます
     */
    @Override // TODO:中央銀行はさらにオーバーライド
        public Entity saveMoney(int amount) {
            account().saveMoney(amount);
            mainBank.keep(amount);
            return this;
        }

    /**
     * お金をおろします
     * 対象はメインバンク
     * 銀行が実行すると中央銀行からおろします
     * 中央銀行が実行すると、新たなお金を作成します
     */
    @Override
    public Entity downMoney(int amount) {
        account().downMoney(amount);
        mainBank.paidOut(amount);
        return this;
    }

    /**
     * 借金をするため、申し込むために使うDebtMediatorオブジェクトを作成します
     * 借金が不成立の場合は想定外
     * {@code
     *  DebtMediator debt = subject.offerDebt(100000);
     *  subject.acceptDebt(debt);
     *  }
     */
    public final DebtMediator offerDebt(int amount) {
        DebtMediator debt = new DebtMediator(account(), amount);
        mDebtList.add(debt);
        return debt;
    }
    /**
     * 借金の申し込むを受け入れ、お金を貸します
     * @return 貸した金額
     */
    public final int acceptDebt(DebtMediator debt) {
        mClaimList.add(debt);
        debt.accepted(account(), MarketInfomation.INSTANCE.nowDate());
        return debt.amount();
    }

    @Override
    public final void payTax(int amount) {
        account().payTax(amount);
    }
    /**
     * 借金を返済します
     * 中央銀行が実行すると、お金が市場から消えます
     */
    public final void repay(int amount) {
        account().repay(amount);
    }
    /**
     * 返済を受けます
     */
    @Override
    public final void repaid(int amount) {
        account().repaid(amount);
    }
}
