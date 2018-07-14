package jp.gr.java_conf.falius.economy2.player;

import java.util.Optional;

import jp.gr.java_conf.falius.economy2.account.WorkerParsonAccount;

public class WorkerParson extends AbstractEntity implements Worker {
    private final WorkerParsonAccount mAccount = WorkerParsonAccount.newInstance();

    private Optional<Organization> mJob = Optional.<Organization> empty();

    public WorkerParson() {
    }

    @Override
    protected final WorkerParsonAccount account() {
        return mAccount;
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

}
