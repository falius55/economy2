package jp.gr.java_conf.falius.economy2.market.aggre;

import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;
import jp.gr.java_conf.falius.economy2.player.WorkerParson;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class MarketAggregater {
    private final NationAggregater mNationAggregater = new NationAggregater();
    private final CentralBankAggregater mCentralBankAggregater = new CentralBankAggregater();
    private final PrivateBusinessAggregater mPrivateBusinessAggregater = new PrivateBusinessAggregater();
    private final WorkerAggregater mWorkerAggregater = new WorkerAggregater();
    private final PrivateBankAggregater mPrivateBankAggregater = new PrivateBankAggregater();

    /**
     * @since 1.0
     */
    public MarketAggregater() {

    }

    /**
     *
     * @param privateBusiness
     * @since 1.0
     */
    public void add(PrivateBusiness privateBusiness) {
        mPrivateBusinessAggregater.add(privateBusiness);
    }

    /**
     *
     * @param worker
     * @since 1.0
     */
    public void add(WorkerParson worker) {
        mWorkerAggregater.add(worker);
    }

    /**
     *
     * @param privateBank
     * @since 1.0
     */
    public void add(PrivateBank privateBank) {
        mPrivateBankAggregater.add(privateBank);
    }

    /**
     * 国内総生産
     * @return
     * @since 1.0
     */
    public int GDP() {
        return mPrivateBusinessAggregater.addedValue()
                + mNationAggregater.salaries()
                + mCentralBankAggregater.salaries();
    }

    /**
     * 分配面からみたGDP
     * @return
     * @since 1.0
     */
    public int sGDP() {
        return mWorkerAggregater.salary()
                + mPrivateBusinessAggregater.benefits() // 在庫投資額含む
                + mPrivateBusinessAggregater.depreciation()
                + mNationAggregater.pureIncome()
                + mPrivateBusinessAggregater.accruedConsumptionTax(); // 消費税の徴税がまだの場合、政府の会計に反映されていない
    }

    /**
     * 国内総支出(支出面からみたGDP)
     * @return
     * @since 1.0
     */
    public int GDE() {
        return C() + I() + G();
    }

    /**
     * 国内純生産
     * @return
     * @since 1.0
     */
    public int NDP() {
        return GDP()
                - mPrivateBusinessAggregater.depreciation();
    }

    /**
     * 国内所得
     * @return
     * @since 1.0
     */
    public int DI() {
        return NDP()
                - (mNationAggregater.pureIncome() + mPrivateBusinessAggregater.accruedConsumptionTax());
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public int C() {
        return mWorkerAggregater.consumption()
                + mCentralBankAggregater.salaries();
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public int I() {
        return mPrivateBusinessAggregater.I();
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public int G() {
        return mNationAggregater.G();
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public int M() {
        return mWorkerAggregater.cashAndDeposits()
                + mPrivateBusinessAggregater.cashAndDeposits();
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public int NationCashAndDeposits() {
        return mNationAggregater.cashAndDeposits();
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public int GovernmentBonds() {
        return mNationAggregater.bonds();
    }

    /**
     *
     * @return
     * @since 1.0
     */
    public int NationPureIncome() {
        return mNationAggregater.pureIncome()
                + mPrivateBusinessAggregater.accruedConsumptionTax();
    }

    /**
     *
     * @since 1.0
     */
    public void clear() {
        mPrivateBusinessAggregater.clear();
        mWorkerAggregater.clear();
        mPrivateBankAggregater.clear();
    }

}
