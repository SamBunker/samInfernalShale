package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.Combat;
import org.powbot.api.rt4.Equipment;
import org.sam.Functions;
import org.sam.Task;
import org.sam.samInfernalShale;

public class SpecialAttack extends Task {
    samInfernalShale main;

    public SpecialAttack(samInfernalShale main) {
        super();
        super.name = "Using Special Attack";
        this.main = main;
    }

    @Override
    public boolean activate() {
        boolean hasSpecialPickaxe = Functions.hasItem("Dragon pickaxe") || Functions.hasItem("Crystal pickaxe") || Functions.hasItem("Infernal pickaxe");
        boolean pickaxeEquipped = Equipment.stream().nameContains("pickaxe").isNotEmpty();
        return !Combat.specialAttack() && Combat.specialPercentage() == 100 && hasSpecialPickaxe && pickaxeEquipped;
    }

    @Override
    public void execute() {
        Combat.specialAttack(true);
        Condition.sleep(Random.nextInt(45, 106));
    }
}
