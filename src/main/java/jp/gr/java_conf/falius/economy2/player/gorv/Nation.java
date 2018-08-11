package jp.gr.java_conf.falius.economy2.player.gorv;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jp.gr.java_conf.falius.economy2.agreement.Bond;
import jp.gr.java_conf.falius.economy2.agreement.PaymentByInstallments;
import jp.gr.java_conf.falius.economy2.book.GovernmentBooks;
import jp.gr.java_conf.falius.economy2.enumpack.GovernmentTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.PrivateBusinessTitle;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.market.Market;
import jp.gr.java_conf.falius.economy2.player.AccountOpenable;
import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;
import jp.gr.java_conf.falius.economy2.player.bank.Bank;
import jp.gr.java_conf.falius.economy2.player.bank.CentralBank;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class Nation implements Government {
    public static final Nation INSTANCE;

    private final GovernmentBooks mBooks;
    /**
     * 未成約の国債
     */
    private final Set<Bond> mBondMarket = new HashSet<>();
    /**
     * 成約済みの国債
     */
    private final Set<Bond> mBonds = new HashSet<>();
    /**
     * 公共事業の後払い
     */
    private final Set<PaymentByInstallments<PrivateBusinessTitle>> mInstallments = new HashSet<>();

    static {
        INSTANCE = new Nation();
    }

    private Nation() {
        mBooks = GovernmentBooks.newInstance(mainBank().nationAccount());
    }

    /**
     * @since 1.0
     */
    @Override
    public GovernmentBooks books() {
        return mBooks;
    }

    /**
     * @since 1.0
     */
    @Override
    public CentralBank mainBank() {
        return CentralBank.INSTANCE;
    }

    /**
     * @since 1.0
     */
    @Override
    public int cash() {
        return mBooks.get(GovernmentTitle.CASH);
    }

    /**
     * @since 1.0
     */
    @Override
    public int deposit() {
        return mBooks.get(GovernmentTitle.DEPOSIT);
    }

    /**
     * @since 1.0
     */
    public Set<Bond> bonds() {
        return Collections.unmodifiableSet(mBonds);
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
        update();
        collectTaxes();
    }

    /**
     * @since 1.0
     */
    private void update() {
        // 不足分の国債発行
        int shortage = expenditureBurden() - deposit();
        if (shortage > 0) {
            issueAndAdvertise(shortage, 100000);
        }

        mInstallments.forEach(pbi -> pbi.update(mBooks));
        Set<PaymentByInstallments<PrivateBusinessTitle>> completed = mInstallments.stream()
                .filter(PaymentByInstallments<PrivateBusinessTitle>::isComplete)
                .collect(Collectors.toSet());
        completed.forEach(mBooks::redeem);
        mInstallments.removeAll(completed);

        mBooks.updateFixedAssets();
    }

    /**
     * 支出負担額を返します。
     * @return
     * @since 1.0
     */
    public int expenditureBurden() {
        return mInstallments.stream()
                .mapToInt(PaymentByInstallments<PrivateBusinessTitle>::remain)
                .sum();
    }

    private void issueAndAdvertise(int allAmount, int faceValue) {
        int numOfIssue = (int) Math.ceil((double) allAmount / faceValue);
        IntStream.generate(() -> faceValue).limit(numOfIssue).forEach(this::issueBonds);
        advertiseBonds();
        makeUnderwriteBonds(CentralBank.INSTANCE);
    }

    /**
     * @since 1.0
     */
    @Override
    public Government issueBonds(int amount) {
        Bond bond = new Bond(mBooks, amount, Period.ofYears(1));
        mBondMarket.add(bond);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public Government redeemBonds(int amount) {
        mBonds.stream()
                .filter(bond -> !bond.isPayOff())
                .sorted((b1, b2) -> b1.deadLine().compareTo(b2.deadLine()))
                .forEach(new Consumer<Bond>() {
                    private int mAmount = amount;

                    @Override
                    public void accept(Bond bond) {
                        if (bond.amount() > mAmount) {
                            return;
                        }
                        if (bond.redeemed()) {
                            mAmount -= bond.amount();
                        }
                    }
                });
        return this;
    }

    /**
     * 債券市場の国債を引き受けさせる。
     * 市中の銀行は財務状況に応じて引き受け、中央銀行は市場に残っているすべての国債を引き受けます。
     * @param bank
     * @return 成約した国債総額
     */
    public int makeUnderwriteBonds(Bank bank) {
        Set<Bond> successed = bank.searchBonds(mBondMarket);
        mBondMarket.removeAll(successed);
        mBonds.addAll(successed);
        return successed.stream().mapToInt(Bond::amount).sum();
    }

    /**
     * 国債発行を公示する
     * @return 成約した国債総額
     */
    public int advertiseBonds() {
        return PrivateBank.stream().mapToInt(this::makeUnderwriteBonds).sum();
    }

    /**
     * @since 1.0
     */
    @Override
    public Government collectTaxes() {
        Market.INSTANCE.employables().forEach(ep -> ep.payIncomeTax(mBooks));
        PrivateBusiness.stream().forEach(pb -> pb.payConsumptionTax(mBooks));
        return this;
    }

    /**
     * @return 買値
     * @since 1.0
     */
    @Override
    public OptionalInt order(Product product) {
        Optional<PrivateBusiness> optStore = PrivateBusiness.stream(Industry.Type.CONTRACT)
                .filter(pb -> pb.canSale(product, 1)).findAny();
        if (!optStore.isPresent()) {
            return OptionalInt.empty();
        }
        PrivateBusiness store = optStore.get();
        Optional<PaymentByInstallments<PrivateBusinessTitle>> optPayment = store.saleByInstallments(product, 1);
        if (!optPayment.isPresent()) {
            return OptionalInt.empty();
        }
        PaymentByInstallments<PrivateBusinessTitle> payment = optPayment.get();
        mInstallments.add(payment);
        return OptionalInt.of(payment.allAmount());
    }

    /**
     * @since 1.0
     */
    @Override
    public AccountOpenable saveMoney(int amount) {
        mBooks.saveMoney(amount);
        mainBank().keepByNation(amount);
        return this;
    }

    /**
     * @since 1.0
     */
    @Override
    public AccountOpenable downMoney(int amount) {
        mBooks.downMoney(amount);
        mainBank().paidOutByNation(amount);
        return this;
    }

    /**
     * @since 1.0
     */
    public void clear() {
        mBooks.clearBook();
        mBondMarket.clear();
        mBonds.clear();
        mInstallments.clear();
    }

}
