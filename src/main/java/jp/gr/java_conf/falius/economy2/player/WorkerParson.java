package jp.gr.java_conf.falius.economy2.player;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

import jp.gr.java_conf.falius.economy2.account.DebtMediator;
import jp.gr.java_conf.falius.economy2.account.WorkerParsonAccount;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.enumpack.WorkerParsonAccountTitle;
import jp.gr.java_conf.falius.economy2.market.Market;

public class WorkerParson extends AbstractEntity implements Worker {
    private final WorkerParsonAccount mAccount = WorkerParsonAccount.newInstance();

    private Employable mJob = null;

    public WorkerParson() {
    }

    @Override
    protected final WorkerParsonAccount account() {
        return mAccount;
    }

    public void borrow(int amount) {
        Optional<PrivateBank> opt = PrivateBank.stream().filter(pb -> pb.canLend(amount)).findAny();
        PrivateBank bank = opt.get();
        DebtMediator dm = super.offerDebt(amount);
        bank.acceptDebt(dm);
    }

    /**
     * 給料を受け取る
     */
    @Override
    public void getSalary(int amount) {
        mAccount.getSalary(amount);
        super.transfered(amount);
    }

    @Override
    public boolean seekJob() {
        Optional<Employable> optEp = Market.INSTANCE.employables()
                .filter(ep -> ep.isRecruit() && (Objects.isNull(mJob) || !mJob.equals(ep)))
                .findAny();
        if (!optEp.isPresent()) {
            return false;
        }
        Employable ep = optEp.get();

        retireJob();
        mJob = ep;
        ep.employ(this);
        return true;
    }

    @Override
    public void retireJob() {
        if (hasJob()) {
            mJob.fire(this);
        }
        mJob = null;
    }

    @Override
    public boolean hasJob() {
        return Objects.nonNull(mJob);
    }

    @Override
    public OptionalInt buy(Product product, int require) {
        Optional<WorkerParsonAccountTitle> optTitle = WorkerParsonAccountTitle.titleFrom(product);
        if (!optTitle.isPresent()) {
            return OptionalInt.empty();
        } // 労働者が買うような代物じゃない
        WorkerParsonAccountTitle title = optTitle.get();

        Optional<PrivateBusiness> optStore = PrivateBusiness.stream(Industry.Type.RETAIL)
                .filter(pb -> pb.canSale(product, require)).findAny();
        if (!optStore.isPresent()) {
            return OptionalInt.empty();
        }
        PrivateBusiness store = optStore.get();

        OptionalInt optPrice = store.saleByCash(product, require);
        if (!optPrice.isPresent()) {
            return OptionalInt.empty();
        }
        int price = optPrice.getAsInt();

        final int cash = mAccount.get(WorkerParsonAccountTitle.CASH);
        if (cash >= price) {
            mAccount.add(title, price);
            return optPrice;
        }

        final int deposit = mAccount.get(WorkerParsonAccountTitle.ORDINARY_DEPOSIT);
        if (cash + deposit >= price) {
            downMoney(price - cash);
            mAccount.add(title, price);
            return optPrice;
        }

        downMoney(deposit);
        borrow(price - (cash + deposit));
        mAccount.add(title, price);
        return optPrice;
    }

    @Override
    public OptionalInt buy(Product product) {
        return buy(product, 1);
    }

    @Override
    public int cash() {
        return mAccount.get(WorkerParsonAccountTitle.CASH);
    }

    @Override
    public int deposit() {
        return mAccount.get(WorkerParsonAccountTitle.ORDINARY_DEPOSIT);
    }

    @Override
    protected Optional<Bank> searchBank() {
        Optional<PrivateBank> opt = PrivateBank.stream().findAny();
        if (!opt.isPresent()) {
            throw new IllegalStateException("market has no banks");
        }
        return Optional.of(opt.get());
    }

    @Override
    public void closeEndOfMonth() {
        // TODO 自動生成されたメソッド・スタブ

    }

    public Optional<PrivateBusiness> establish(Industry industry, int initialCapital) {
        int cash = mAccount.get(WorkerParsonAccountTitle.CASH);
        int deposit = mAccount.get(WorkerParsonAccountTitle.ORDINARY_DEPOSIT);
        if (cash + deposit < initialCapital) {
            return Optional.empty();
        }

        if (deposit < initialCapital) {
            int shortfall = initialCapital - deposit;
            super.saveMoney(shortfall);
        }
        mAccount.establish(initialCapital);
        super.transfer(initialCapital);
        return Optional.of(new PrivateBusiness(industry, initialCapital));
    }

}
