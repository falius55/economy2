package jp.gr.java_conf.falius.economy2.player;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

import jp.gr.java_conf.falius.economy2.account.PrivateAccount;
import jp.gr.java_conf.falius.economy2.agreement.Loan;
import jp.gr.java_conf.falius.economy2.book.WorkerParsonBooks;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.enumpack.WorkerParsonTitle;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class WorkerParson implements Worker, PrivateEntity, Borrowable {
    private final PrivateBank mMainBank;
    private final WorkerParsonBooks mBooks;
    private final Set<Loan> mLoans = new HashSet<>();

    private Employable mJob = null;

    /**
     * @since 1.0
     */
    public WorkerParson() {
        mMainBank = searchBank();
        PrivateAccount account = mMainBank.createAccount(this);
        mBooks = WorkerParsonBooks.newInstance(account);
        Market.INSTANCE.aggregater().add(this);
    }

    /**
     * @since 1.0
     */
    private PrivateBank searchBank() {
        Optional<PrivateBank> opt = Market.INSTANCE.entities(PrivateBank.class).findAny();
        if (!opt.isPresent()) {
            throw new IllegalStateException("market has no banks");
        }
        return opt.get();
    }

    /**
     * @since 1.0
     */
    @Override
    public final WorkerParsonBooks books() {
        return mBooks;
    }

    /**
     * @since 1.0
     */
    @Override
    public PrivateBank mainBank() {
        return mMainBank;
    }

    /**
     * @since 1.0
     */
    @Override
    public int cash() {
        return mBooks.get(WorkerParsonTitle.CASH);
    }

    /**
     * @since 1.0
     */
    @Override
    public int deposit() {
        return mBooks.get(WorkerParsonTitle.ORDINARY_DEPOSIT);
    }

    /**
     * @since 1.0
     */
    @Override
    public boolean hasJob() {
        return Objects.nonNull(mJob);
    }

    /**
     * 日課処理を行います。
     * @since 1.0
     */
    public void closeEndOfDay(LocalDate date) {

    }

    /**
     * @since 1.0
     */
    @Override
    public void closeEndOfMonth() {
        // TODO 自動生成されたメソッド・スタブ

    }

    /**
     * @since 1.0
     */
    @Override
    public OptionalInt buy(Product product) {
        return buy(product, 1);
    }

    /**
     * @since 1.0
     */
    @Override
    public OptionalInt buy(Product product, int require) {
        Optional<WorkerParsonTitle> optTitle = WorkerParsonTitle.titleFrom(product);
        if (!optTitle.isPresent()) {
            return OptionalInt.empty();
        } // 労働者が買うような代物じゃない
        WorkerParsonTitle title = optTitle.get();

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
            mBooks.buyOnCash(title, price);
            return optPrice;
        }

        final int deposit = deposit();
        if (cash + deposit >= price) {
            downMoney(price - cash);
            mBooks.buyOnCash(title, price);
            return optPrice;
        }

        borrow(price - (cash + deposit));
        downMoney(deposit());
        mBooks.buyOnCash(title, price);
        return optPrice;
    }

    /**
     * @since 1.0
     */
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

    /**
     * @since 1.0
     */
    @Override
    public void retireJob() {
        if (hasJob()) {
            mJob.fire(this);
        }
        mJob = null;
    }

    /**
     * @since 1.0
     */
    @Override
    public AccountOpenable saveMoney(int amount) {
        mBooks.saveMoney(amount);
        mainBank().keep(this, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public AccountOpenable downMoney(int amount) {
        mBooks.downMoney(amount);
        mainBank().paidOut(this, amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public boolean borrow(int amount) {
        Optional<PrivateBank> opt = Market.INSTANCE
                .entities(PrivateBank.class)
                .filter(pb -> pb.canLend(amount))
                .findAny();
        if (!opt.isPresent()) { return false; }
        PrivateBank bank = opt.get();
        Loan loan = offerDebt(amount);
        bank.acceptDebt(loan);
        return true;
    }

    /**
     * 借金をするため、申し込むために使うDebtMediatorオブジェクトを作成する
     * 借金が不成立の場合は想定外
     * @since 1.0
     */
    private Loan offerDebt(int amount) {
        Loan debt = new Loan(this, amount, Period.ofYears(1));
        mLoans.add(debt);
        return debt;
    }

    /**
     * 借金を返済します
     * @since 1.0
     */
    @Override
    public void repay(int amount) {
    }

    /**
     *
     * @param industry
     * @param initialCapital
     * @return
     * @since 1.0
     */
    public Optional<PrivateBusiness> establish(Industry industry, int initialCapital) {
        return establish(industry, industry.products(), initialCapital);
    }

    /**
     *
     * @param industry
     * @param products
     * @param initialCapital
     * @return
     * @since 1.0
     */
    public Optional<PrivateBusiness> establish(Industry industry, Set<Product> products, int initialCapital) {
        int cash = mBooks.get(WorkerParsonTitle.CASH);
        int deposit = mBooks.get(WorkerParsonTitle.ORDINARY_DEPOSIT);
        if (cash + deposit < initialCapital) {
            return Optional.empty();
        }

        if (deposit < initialCapital) {
            int shortage = initialCapital - deposit;
            saveMoney(shortage);
        }
        mBooks.establish(initialCapital);
        PrivateBusiness business = new PrivateBusiness(this, industry, products, initialCapital);
        mainBank().account(this).transfer(business.mainBank().account(business), initialCapital);
        retireJob();
        return Optional.of(business);
    }

}
