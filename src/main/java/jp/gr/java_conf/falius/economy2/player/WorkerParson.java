package jp.gr.java_conf.falius.economy2.player;

import java.util.Optional;
import java.util.OptionalInt;

import jp.gr.java_conf.falius.economy2.account.DebtMediator;
import jp.gr.java_conf.falius.economy2.account.WorkerParsonAccount;
import jp.gr.java_conf.falius.economy2.enumpack.Industry;
import jp.gr.java_conf.falius.economy2.enumpack.Product;
import jp.gr.java_conf.falius.economy2.enumpack.WorkerParsonAccountTitle;

public class WorkerParson extends AbstractEntity implements Worker {
    private final WorkerParsonAccount mAccount = WorkerParsonAccount.newInstance();

    private Optional<Organization> mJob = Optional.<Organization> empty();

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
    public void getPaied(int amount) {
        mAccount.getPaied(amount);
    }

    @Override
    public boolean seekJob() {
        Optional<Organization> opt = PrivateBusiness.stream()
                .map(pb -> (Organization) pb)
                .filter(pb -> !mJob.equals(Optional.of(pb)) && pb.isRecruit())
                .findAny();

        if (opt.isPresent()) {
            retireJob();
            mJob = opt;
            mJob.ifPresent(pb -> pb.employ(this));
            return true;
        }
        return false;
    }

    @Override
    public void retireJob() {
        mJob.ifPresent(pb -> pb.fire(this));
        mJob = Optional.empty();
    }

    @Override
    public boolean hasJob() {
        return mJob.isPresent();
    }

    @Override
    public void buy(Product product, int require) {
        Optional<WorkerParsonAccountTitle> optTitle = WorkerParsonAccountTitle.titleFrom(product);
        if (!optTitle.isPresent()) { return; }  // 労働者が買うような代物じゃない
        WorkerParsonAccountTitle title = optTitle.get();

        Optional<PrivateBusiness> optStore = PrivateBusiness.stream(Industry.Type.RETAIL)
                .filter(pb -> pb.canSale(product, require)).findAny();
        if (!optStore.isPresent()) {
            return;
        }
        PrivateBusiness store = optStore.get();

        OptionalInt optPrice = store.sale(product, require);
        if (!optPrice.isPresent()) {
            return;
        }
        int price = optPrice.getAsInt();

        final int cash = mAccount.get(WorkerParsonAccountTitle.CASH);
        if (cash >= price) {
            mAccount.add(title, price);
            return;
        }

        final int deposit = mAccount.get(WorkerParsonAccountTitle.ORDINARY_DEPOSIT);
        if (cash + deposit >= price) {
            downMoney(price - cash);
            mAccount.add(title, price);
            return;
        }

        downMoney(deposit);
        borrow(price - (cash + deposit));
        mAccount.add(title, price);
    }

    @Override
    public void buy(Product product) {
        buy(product, 1);
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
    protected Bank searchBank() {
        return PrivateBank.stream().findAny().get();
    }

}
