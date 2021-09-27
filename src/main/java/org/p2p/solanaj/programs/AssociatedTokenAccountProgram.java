package org.p2p.solanaj.programs;

import org.p2p.solanaj.core.AccountMeta;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.TransactionInstruction;

import java.util.ArrayList;
import java.util.List;

public class AssociatedTokenAccountProgram {
    public static final PublicKey PROGRAM_ID = new PublicKey("ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL");
    public static final PublicKey RENT_PROGRAM_ID = new PublicKey("SysvarRent111111111111111111111111111111111");

    public static TransactionInstruction createAssociatedTokenAccount(PublicKey funder, PublicKey owner, String mint) throws Exception {
        ArrayList<AccountMeta> keys = new ArrayList<>();

        List<byte[]> seeds = new ArrayList<>();
        seeds.add(owner.toByteArray());
        seeds.add(SplTokenProgram.PROGRAM_ID.toByteArray());
        seeds.add(new PublicKey(mint).toByteArray());

        PublicKey.ProgramDerivedAddress derivedAddress = PublicKey.findProgramAddress(seeds, PROGRAM_ID);
        keys.add(new AccountMeta(funder, true, true));
        keys.add(new AccountMeta(derivedAddress.getAddress(), false, true));
        keys.add(new AccountMeta(owner, false, false));
        keys.add(new AccountMeta(new PublicKey(mint), false, false));
        keys.add(new AccountMeta(SystemProgram.PROGRAM_ID, false, false));
        keys.add(new AccountMeta(SplTokenProgram.PROGRAM_ID, false, false));
        keys.add(new AccountMeta(RENT_PROGRAM_ID, false, false));

        return new TransactionInstruction(PROGRAM_ID, keys, new byte[0]);
    }
}
