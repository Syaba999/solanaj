package org.p2p.solanaj.programs;

import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.TransactionInstruction;
import org.p2p.solanaj.core.AccountMeta;

import java.util.ArrayList;

import static org.bitcoinj.core.Utils.*;

public class SplTokenProgram {
    public static final PublicKey PROGRAM_ID = new PublicKey("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA");

    public static final int PROGRAM_INDEX_TRANSFER_SPL = 3;

    public static TransactionInstruction transferSplToken(String fromAddress, String toAddress, PublicKey owner, long lamports) {
        ArrayList<AccountMeta> keys = new ArrayList<>();
        keys.add(new AccountMeta(new PublicKey(fromAddress), false, true));
        keys.add(new AccountMeta(new PublicKey(toAddress), false, true));
        keys.add(new AccountMeta(owner, true, true));

        byte[] data = new byte[4 + 8];
        uint32ToByteArrayLE(PROGRAM_INDEX_TRANSFER_SPL, data, 0);
        int64ToByteArrayLE(lamports, data, 4);

        return new TransactionInstruction(PROGRAM_ID, keys, data);
    }

}
