@CheckInfo(
        type = CheckType.BAD_PACKETS,
        subType = "G3",
        friendlyName = "Ping Spoof",
        minViolations = -3.0,
        maxViolations = 30,
        version = CheckVersion.RELEASE,
        logData = true
)
public class BadPacketsG3 extends Check implements PacketHandler {

    private boolean receivedKeepAlive;

    public void handle(VPacketPlayInFlying vPacket) {
        if (this.receivedKeepAlive) {
            double highest = MathUtil.highest(this.playerData.getLastTransactionPing(), this.playerData.getTransactionPing(), this.playerData.getAverageTransactionPing());
            double lowest = MathUtil.lowest(this.playerData.getLastPing(), this.playerData.getPing(), this.playerData.getAveragePing());
            if (lowest - highest > 50.0 + highest / 4.0) {
                double buff;
                if (StorageEngine.getInstance().getVerusConfig().isMoreTransactions()) {
                    buff = 0.25;
                } else {
                    buff = 1.0;
                }

                this.handleViolation(() -> String.format("K(%s) T(%s) %s", lowest, highest, vPacket.isPos()), buff);
            } else {
                double vl;
                if (StorageEngine.getInstance().getVerusConfig().isMoreTransactions()) {
                    vl = 0.3;
                } else {
                    vl = 0.1;
                }

                this.decreaseVL(vl);
            }
            this.receivedKeepAlive = false;
        }
    }

    public void handle(VPacketPlayInKeepAlive vPacket) {
        this.receivedKeepAlive = true;
    }

}