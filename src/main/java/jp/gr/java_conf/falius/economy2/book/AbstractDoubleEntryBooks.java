package jp.gr.java_conf.falius.economy2.book;

import java.time.LocalDate;

import jp.gr.java_conf.falius.economy2.enumpack.Title;
import jp.gr.java_conf.falius.economy2.enumpack.TitleType;

/**
 * 複式簿記を表すすべてのクラスの基底クラス
 * @author "ymiyauchi"
 *
 * @param <T>
 * @since 1.0
 */
public abstract class AbstractDoubleEntryBooks<T extends Enum<T> & Title>
        extends AbstractBooks<T> implements DoubleEntryBooks<T> {
    private final FixedAssetManager mFixedAssetManager;
    private final boolean mByDirect;

    /**
     *
     * @param clazz 利用する勘定科目の列挙クラス
     * @param byDirect 直説法で減価償却するならtrue、間接法ならfalse
     * @since 1.0
     */
    protected AbstractDoubleEntryBooks(Class<T> clazz, boolean byDirect) {
        super(clazz);
        mFixedAssetManager = new FixedAssetManager();
        mByDirect = byDirect;
    }

    /**
     * @since 1.0
     */
    @Override
    public void clear() {
        super.clear();
        mFixedAssetManager.clear();
    }

    /**
     * 帳簿に記入します
     * @param rl 記入箇所。借方(LEFT)か貸し方(RIGHT)か
     * @param item 勘定科目
     * @param amount 金額
     * @since 1.0
     */
    private final void add(TitleType.RL rl, T item, int amount) {
        if (item.type().rl().equals(rl)) {
            super.increase(item, amount);
        } else {
            super.decrease(item, amount);
        }
    }

    /**
     * 借方に記入します
     * @param item 勘定科目
     * @param amount 金額
     * @since 1.0
     */
    @Override
    public final void addLeft(T item, int amount) {
        add(TitleType.RL.LEFT, item, amount);
    }

    /**
     * 貸方に記入します
     * @param item 勘定科目
     * @param amount 金額
     * @since 1.0
     */
    @Override
    public final void addRight(T item, int amount) {
        add(TitleType.RL.RIGHT, item, amount);
    }

    // 以下は固定資産

    @Override
    public boolean byDirect() {
        return mByDirect;
    }

    /**
     * 固定資産を追加します
     * @param dateOfAcquisition 取得日
     * @param acquisitionCost 取得原価
     * @param serviceLife 耐用年数
     * @since 1.0
     */
    protected void addFixedAsset(LocalDate dateOfAcquisition, int acquisitionCost, int serviceLife) {
        mFixedAssetManager.add(dateOfAcquisition, acquisitionCost, serviceLife);
    }

    public int updateFixedAssets() {
        int amount = mFixedAssetManager.update();
        if (mByDirect) {
            depreciationByDirect(amount);
        } else {
            depreciationByIndirect(amount);
        }
        return amount;
    }

    /**
     * 間接法で減価償却する
     * @param amount
     * @since 1.0
     */
    protected abstract void depreciationByIndirect(int amount);

    /**
     * 直接法で減価償却する
     * @since 1.0
     */
    protected abstract void depreciationByDirect(int amount);

    /**
     * 保有している固定資産の現在価値の総額を計算します
     * @since 1.0
     */
    public int fixedAssetsValue() {
        return mFixedAssetManager.presentValue();
    }

}
