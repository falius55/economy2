package jp.gr.java_conf.falius.economy2.account;

import java.time.LocalDate;

import jp.gr.java_conf.falius.economy2.enumpack.AccountTitle;
import jp.gr.java_conf.falius.economy2.enumpack.AccountType;

public abstract class AbstractDoubleEntryAccount<T extends Enum<T> & AccountTitle>
        extends AbstractAccount<T> implements DoubleEntryAccount<T> {
    private final FixedAssetManager mFixedAssetManager;

    protected AbstractDoubleEntryAccount(Class<T> clazz) {
        super(clazz);
        mFixedAssetManager = new FixedAssetManager();
    }

    /**
     * 帳簿に記入します
     * @param rl 記入箇所。借方(LEFT)か貸し方(RIGHT)か
     * @param item 勘定科目
     * @param amount 金額
     */
    private final void add(AccountType.RL rl, T item, int amount) {
        if (item.type().rl().equals(rl)) {
            super.increase(item, amount);
        } else {
            super.decrease(item, amount);
        }
    }

    /**
     * 標準資産科目(defaultItem()によって定義)を相手科目として、指定された科目を増加させます
     * @param item 勘定科目
     * @param amount 金額
     * @throws IllegalArgumentException サブタイプで定義した標準科目が資産科目でない場合
     */
    @Override
    public final void add(T item, int amount) {
        T defaultItem = defaultItem();
        if (!defaultItem.type().equals(AccountType.ASSETS)) { throw new IllegalArgumentException("defaultItem is not Assets"); }
        add(item.type().rl(), item, amount);
        add(item.type().rl().inverse(), defaultItem, amount);
    }

    /**
     * 借方に記入します
     * @param item 勘定科目
     * @param amount 金額
     */
    @Override
    public final void addLeft(T item, int amount) {
        add(AccountType.RL.LEFT, item, amount);
    }

    /**
     * 貸方に記入します
     * @param item 勘定科目
     * @param amount 金額
     */
    @Override
    public final void addRight(T item, int amount) {
        add(AccountType.RL.RIGHT, item, amount);
    }

    // 以下は固定資産

    /**
     * 固定資産を追加します
     * @param mDateOfAcquisition 取得日
     * @param mAcquisitionCost 取得原価
     * @param mServiceLife 耐用年数
     */
    protected final void addFixedAsset(LocalDate dateOfAcquisition, int acquisitionCost, int serviceLife) {
        mFixedAssetManager.add(dateOfAcquisition, acquisitionCost, serviceLife);
        // mFixedAssets.add(new FixedAsset(dateOfAcquisition, acquisitionCost, serviceLife));
    }

    /**
     * 所有している固定資産全てにおいて、減価償却の処理を行います
     * より具体的には、dateが償却日である固定資産のみ減価償却し、その償却費の総額を返します。
     * ただし、帳簿への記帳処理は行いません
     * @param date 記入日
     * @return その日の償却額
     */
    protected final int recordFixedAssets(LocalDate date) {
        return mFixedAssetManager.record(date);
    }

    /**
     * 保有している固定資産の現在価値の総額を計算します
     */
    protected final int fixedAssetsValue() {
        return mFixedAssetManager.presentValue();
    }

}
