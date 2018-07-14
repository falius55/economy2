package jp.gr.java_conf.falius.economy2.account;

import jp.gr.java_conf.falius.economy2.enumpack.AccountType;
import jp.gr.java_conf.falius.economy2.enumpack.WorkerParsonAccountTitle;

public class WorkerParsonAccount extends AbstractAccount<WorkerParsonAccountTitle> {

    public static WorkerParsonAccount newInstance() {
        return new WorkerParsonAccount();
    }

    private WorkerParsonAccount() {
        super(WorkerParsonAccountTitle.class);
    }

    @Override
    public WorkerParsonAccountTitle[] items() {
        return WorkerParsonAccountTitle.values();
    }

    @Override
    public WorkerParsonAccountTitle defaultItem() {
        return WorkerParsonAccountTitle.defaultItem();
    }

    @Override
    public void add(WorkerParsonAccountTitle item, int amount) {
        WorkerParsonAccountTitle defaultItem = defaultItem();
        if (!defaultItem.type().equals(AccountType.ASSETS)) {
            throw new IllegalArgumentException("defaultItem is not Assets");
        }

        super.increase(item, amount);

        switch (item.type()) {
        case REVENUE:
        case EQUITY:
        case LIABILITIES:
            super.increase(defaultItem, amount);
            break;

        case EXPENSE:
        case ASSETS:
            super.decrease(defaultItem, amount);
            break;

        default:
            break;
        }

    }

    @Override
    public Account<WorkerParsonAccountTitle> saveMoney(int amount) {
        add(WorkerParsonAccountTitle.ORDINARY_DEPOSIT, amount);
        return this;
    }

    @Override
    public Account<WorkerParsonAccountTitle> downMoney(int amount) {
        super.decrease(WorkerParsonAccountTitle.CASH, amount);
        super.increase(WorkerParsonAccountTitle.ORDINARY_DEPOSIT, amount);
        return this;
    }

    @Override
    public Account<WorkerParsonAccountTitle> borrow(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Account<WorkerParsonAccountTitle> repay(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Account<WorkerParsonAccountTitle> lend(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Account<WorkerParsonAccountTitle> repaid(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Account<WorkerParsonAccountTitle> payTax(int amount) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    public Account<WorkerParsonAccountTitle> getPaied(int amount) {
        super.increase(WorkerParsonAccountTitle.SALARIES, amount);
        super.increase(WorkerParsonAccountTitle.ORDINARY_DEPOSIT, amount);
        return this;
    }

}
