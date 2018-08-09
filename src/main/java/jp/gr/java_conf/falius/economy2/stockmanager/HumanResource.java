package jp.gr.java_conf.falius.economy2.stockmanager;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import jp.gr.java_conf.falius.economy2.agreement.Contract;
import jp.gr.java_conf.falius.economy2.agreement.Deferment;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.player.HumanResourcesDepartment;
import jp.gr.java_conf.falius.economy2.player.PrivateBusiness;
import jp.gr.java_conf.falius.economy2.player.Worker;

/**
 *
 * @author "ymiyauchi"
 * @since 1.0
 *
 */
public class HumanResource implements StockManager {
    /**
     * 一件当たり何人の従事者が必要か
     */
    private static final int NUM_OF_WORKER_PER_UNIT = 1;
    private final Product mProduct;
    private final HumanResourcesDepartment mStuffs;
    /**
     * 契約中の請負契約。従事している従業員の管理など
     */
    private final Set<Contract> mContracts = new HashSet<>();
    /**
     * 未計上の仕入れの買掛金
     */
    private final Set<Deferment> mPurchasePayable = new HashSet<>();

    /**
     *
     * @param product
     * @param stuffs
     * @since 1.0
     */
    public HumanResource(Product product, HumanResourcesDepartment stuffs) {
        mProduct = product;
        mStuffs = stuffs;
    }

    /**
     * @since 1.0
     */
    @Override
    public boolean canShipOut(int num) {
        return remains().size() >= num * NUM_OF_WORKER_PER_UNIT;
    }

    /**
     * @since 1.0
     */
    @Override
    public OptionalInt shipOut(int num) {
        if (!canShipOut(num)) {
            return OptionalInt.empty();
        }
        for (int i = 0; i < num; i++) {
            boolean tmp = addContract(NUM_OF_WORKER_PER_UNIT);
            if (!tmp) {
                return OptionalInt.of(mProduct.lotCost() * i);
            }
        }
        return OptionalInt.of(mProduct.lotCost() * num);
    }

    private boolean addContract(int numOfWorker) {
        Set<Worker> remains = remains();
        if (remains.size() < numOfWorker) {
            return false;
        }
        Set<Worker> workers = remains.stream().limit(numOfWorker).collect(Collectors.toSet());
        Contract contract = new Contract(mProduct, workers);
        mContracts.add(contract);
        return true;
    }

    /**
     * 既存の請負契約に従事していない労働者の集合を返します。
     * @since 1.0
     */
    private Set<Worker> remains() {
        update();
        Set<Worker> busy = mContracts.stream()
                .map(Contract::employers)
                .flatMap(Set::stream) // Set<Worker>のstreamをWorkerのstreamにする
                .collect(Collectors.toSet());
        Set<Worker> ret = new HashSet<>(mStuffs.employers());
        ret.removeAll(busy);
        return ret;
    }

    /**
     * @since 1.0
     */
    @Override
    public Set<Deferment> purchasePayable() {
        update();
        Set<Deferment> ret = new HashSet<>(mPurchasePayable);
        mPurchasePayable.clear();
        return ret;
    }

    /**
     * @since 1.0
     */
    @Override
    public int stockCost() {
        return 0;
    }

    /**
     * @since 1.0
     */
    @Override
    public void update() {
        supplyMaterialAll();
        Set<Contract> completed = mContracts.stream()
                .filter(Contract::isComplete)
                .peek(Contract::breakUp)
                .collect(Collectors.toSet());
        mContracts.removeAll(completed);
    }

    private void supplyMaterialAll() {
        mContracts.stream().forEach(this::supplyMaterial);
    }

    private void supplyMaterial(Contract contract) {
        Map<Product, Integer> shortages = contract.shortageMaterials();
        shortages.forEach((material, num) -> {
            boolean isDone = purchase(material, num);
            if (isDone) {
                contract.supplyMaterial(material, num);
            }
        });
    }

    /**
     * 仕入れます
     * @return 仕入れを行えばtrue
     * @since 1.0
     */
    private boolean purchase(Product product, int require) {
        if (require <= 0) {
            return false;
        }
        Optional<PrivateBusiness> optStore = PrivateBusiness.stream(Industry.Type.FARMER)
                .filter(e -> e.canSale(product, require))
                .findAny();
        if (!optStore.isPresent()) { return false; }
        PrivateBusiness store = optStore.get();
        store.update();

        Optional<Deferment> optDeferment = store.saleByReceivable(product, require);
        if (!optDeferment.isPresent()) {
            return false;
        }
        Deferment deferment = optDeferment.get();
        mPurchasePayable.add(deferment);
        return true;
    }

}
