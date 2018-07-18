package jp.gr.java_conf.falius.economy2.player;

import java.time.Period;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

import jp.gr.java_conf.falius.economy2.account.Account;
import jp.gr.java_conf.falius.economy2.account.WorkerParsonAccount;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.enumpack.WorkerParsonAccountTitle;
import jp.gr.java_conf.falius.economy2.loan.Loan;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

public class WorkerParson implements Worker, AccountOpenable, PrivateEntity, Borrowable {
    private final WorkerParsonAccount mAccount = WorkerParsonAccount.newInstance();
    private final PrivateBank mMainBank;
    private final Set<Loan> mLoans = new HashSet<>();

    private Employable mJob = null;

    public WorkerParson() {
        mMainBank = searchBank();
    }

    private PrivateBank searchBank() {
        Optional<PrivateBank> opt = PrivateBank.stream().findAny();
        if (!opt.isPresent()) {
            throw new IllegalStateException("market has no banks");
        }
        return opt.get();
    }

    @Override
    public final Account<WorkerParsonAccountTitle> accountBook() {
        return mAccount;
    }

    @Override
    public PrivateBank mainBank() {
        return mMainBank;
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
    public boolean hasJob() {
        return Objects.nonNull(mJob);
    }

    @Override
    public void closeEndOfMonth() {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public OptionalInt buy(Product product) {
        return buy(product, 1);
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

        final int cash = cash();
        if (cash >= price) {
            mAccount.buyOnCash(title, price);
            return optPrice;
        }

        final int deposit = deposit();
        if (cash + deposit >= price) {
            downMoney(price - cash);
            mAccount.buyOnCash(title, price);
            return optPrice;
        }

        borrow(price - (cash + deposit));
        downMoney(deposit());
        mAccount.buyOnCash(title, price);
        return optPrice;
    }

    /**
     * 給料を受け取る
     */
    @Override
    public void getSalary(int amount) {
        mAccount.getSalary(amount);
        mMainBank.transfered(amount);
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
    public AccountOpenable saveMoney(int amount) {
        mAccount.saveMoney(amount);
        mainBank().keep(amount);
        return this;
    }

    @Override
    public AccountOpenable downMoney(int amount) {
        mAccount.downMoney(amount);
        mainBank().paidOut(amount);
        return this;
    }

    @Override
    public void payTax(int amount) {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public boolean borrow(int amount) {
        Optional<PrivateBank> opt = PrivateBank.stream().filter(pb -> pb.canLend(amount)).findAny();
        if (!opt.isPresent()) { return false; }
        PrivateBank bank = opt.get();
        Loan loan = offerDebt(amount);
        bank.acceptDebt(loan);
        mainBank().transfered(amount);
        return true;
    }

    /**
     * 借金をするため、申し込むために使うDebtMediatorオブジェクトを作成する
     * 借金が不成立の場合は想定外
     */
    private Loan offerDebt(int amount) {
        Loan debt = new Loan(mAccount, amount, Period.ofYears(1));
        mLoans.add(debt);
        return debt;
    }

    /**
     * 借金を返済します
     */
    public void repay(int amount) {
        mAccount.repay(amount);
    }

    public Optional<PrivateBusiness> establish(Industry industry, int initialCapital) {
        return establish(industry, industry.products(), initialCapital);
    }

    public Optional<PrivateBusiness> establish(Industry industry, Set<Product> products, int initialCapital) {
        int cash = mAccount.get(WorkerParsonAccountTitle.CASH);
        int deposit = mAccount.get(WorkerParsonAccountTitle.ORDINARY_DEPOSIT);
        if (cash + deposit < initialCapital) {
            return Optional.empty();
        }

        if (deposit < initialCapital) {
            int shortfall = initialCapital - deposit;
            saveMoney(shortfall);
        }
        mAccount.establish(initialCapital);
        mMainBank.transfer(initialCapital);
        retireJob();
        return Optional.of(new PrivateBusiness(this, industry, products, initialCapital));
    }

}
