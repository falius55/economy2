package jp.gr.java_conf.falius.economy2.market.aggre;

import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;
import jp.gr.java_conf.falius.economy2.player.WorkerParson;
import jp.gr.java_conf.falius.economy2.player.bank.PrivateBank;

public class MarketAggregater {
    private final NationAggregater mNationAggregater = new NationAggregater();
    private final CentralBankAggregater mCentralBankAggregater = new CentralBankAggregater();
    private final PrivateBusinessAggregater mPrivateBusinessAggregater = new PrivateBusinessAggregater();
    private final WorkerAggregater mWorkerAggregater = new WorkerAggregater();
    private final PrivateBankAggregater mPrivateBankAggregater = new PrivateBankAggregater();

    public MarketAggregater() {

    }

    public void add(PrivateBusiness privateBusiness) {
        mPrivateBusinessAggregater.add(privateBusiness);
    }

    public void add(WorkerParson worker) {
        mWorkerAggregater.add(worker);
    }

    public void add(PrivateBank privateBank) {
        mPrivateBankAggregater.add(privateBank);
    }

    /**
     * 国内総生産
     * @return
     */
    public int GDP() {
        return mPrivateBusinessAggregater.addedValue()
                + mNationAggregater.salaries()
                + mCentralBankAggregater.salaries();
    }

    /**
     * 分配面からみたGDP
     * @return
     */
    public int sGDP() {
        return mWorkerAggregater.salary()
                + mPrivateBusinessAggregater.benefits()
                + mPrivateBusinessAggregater.depreciation()
                + mNationAggregater.pureIncome()
                + mPrivateBusinessAggregater.accruedConsumptionTax(); // 消費税の徴税がまだの場合、政府の会計に反映されていない
    }

    /**
     * 国内総支出(支出面からみたGDP)
     * @return
     */
    public int GDE() {
        return C() + I() + G();
    }

    /**
     * 国内純生産
     * @return
     */
    public int NDP() {
        return GDP()
                - mPrivateBusinessAggregater.depreciation();
    }

    /**
     * 国内所得
     * @return
     */
    public int DI() {
        return NDP()
                - (mNationAggregater.pureIncome() + mPrivateBusinessAggregater.accruedConsumptionTax());
    }

    public int C() {
        return mWorkerAggregater.consumption()
                + mCentralBankAggregater.salaries();
    }

    public int I() {
        return mPrivateBusinessAggregater.I();
    }

    public int G() {
        return mNationAggregater.G();
    }

    public int M() {
        return mWorkerAggregater.cashAndDeposits()
                + mPrivateBusinessAggregater.cashAndDeposits();
    }

    public int NationCashAndDeposits() {
        return mNationAggregater.cashAndDeposits();
    }

    public int GovernmentBonds() {
        return mNationAggregater.bonds();
    }

    public int NationPureIncome() {
        return mNationAggregater.pureIncome()
                + mPrivateBusinessAggregater.accruedConsumptionTax();
    }

    public void clear() {
        mPrivateBusinessAggregater.clear();
        mWorkerAggregater.clear();
        mPrivateBankAggregater.clear();
    }

}
