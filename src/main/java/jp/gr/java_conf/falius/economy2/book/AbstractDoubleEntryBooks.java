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

    /**
     *
     * @param clazz
     * @since 1.0
     */
    protected AbstractDoubleEntryBooks(Class<T> clazz) {
        super(clazz);
        mFixedAssetManager = new FixedAssetManager();
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

    /**
     * 固定資産を追加します
     * @param dateOfAcquisition 取得日
     * @param acquisitionCost 取得原価
     * @param serviceLife 耐用年数
     * @since 1.0
     */
    protected final void addFixedAsset(LocalDate dateOfAcquisition, int acquisitionCost, int serviceLife) {
        mFixedAssetManager.add(dateOfAcquisition, acquisitionCost, serviceLife);
    }

    /**
     * 所有している固定資産全てにおいて、減価償却の処理を行います
     * より具体的には、dateが償却日である固定資産のみ減価償却し、その償却費の総額を返します。
     * ただし、帳簿への記帳処理は行いません
     * @param date 記入日
     * @return その日の償却額
     * @since 1.0
     */
    protected final int recordFixedAssets(LocalDate date) {
        return mFixedAssetManager.record(date);
    }

    /**
     * 保有している固定資産の現在価値の総額を計算します
     * @since 1.0
     */
    protected final int fixedAssetsValue() {
        return mFixedAssetManager.presentValue();
    }

}
