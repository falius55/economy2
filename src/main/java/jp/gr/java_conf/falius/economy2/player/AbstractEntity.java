package jp.gr.java_conf.falius.economy2.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jp.gr.java_conf.falius.economy2.account.Account;
import jp.gr.java_conf.falius.economy2.account.DebtMediator;
import jp.gr.java_conf.falius.economy2.market.Market;

public abstract class AbstractEntity implements Entity {
    private Bank mMainBank = null;

    private List<DebtMediator> mDebtList; // 借金のリスト
    private List<DebtMediator> mClaimList; // 貸金のリスト

    AbstractEntity() {
        mDebtList = new ArrayList<DebtMediator>();
        mClaimList = new ArrayList<DebtMediator>();

        searchBank().ifPresent(bank -> mMainBank = bank);
    }

    protected abstract Account<? extends Enum<?>> account();

    protected abstract Optional<Bank> searchBank();

    /**
     * 貯金します
     * 対象はメインバンク
     * 銀行が実行すると中央銀行に預けます
     */
    @Override
    public Entity saveMoney(int amount) {
        account().saveMoney(amount);
        mMainBank.keep(amount);
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
        mMainBank.paidOut(amount);
        return this;
    }

    /**
     * 借金をするため、申し込むために使うDebtMediatorオブジェクトを作成する
     * 政府から実行された場合は公債を発行します
     * 銀行から実行された場合は、コールナイトオーバー物で一時的な借受を申し込みます
     * 中央銀行から実行されると、お金を作成して負債として計上します
     * 借金が不成立の場合は想定外
     * {@code
     *  DebtMediator debt = subject.offerDebt(100000);
     *  subject.acceptDebt(debt);
     *  }
     */
    protected final DebtMediator offerDebt(int amount) {
        DebtMediator debt = new DebtMediator(account(), amount);
        mDebtList.add(debt);
        return debt;
    }

    /**
     * 借金の申し込むを受け入れ、お金を貸します
     * @return 貸した金額
     */
    protected final int acceptDebt(DebtMediator debt) {
        mClaimList.add(debt);
        debt.accepted(account(), Market.INSTANCE.nowDate());
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

    public void transfer(int amount) {
        mMainBank.transfer(amount);
    }

    public void transfered(int amount) {
        mMainBank.transfered(amount);
    }
}
