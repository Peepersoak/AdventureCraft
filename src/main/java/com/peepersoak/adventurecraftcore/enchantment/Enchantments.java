package com.peepersoak.adventurecraftcore.enchantment;

import java.util.ArrayList;
import java.util.List;

public class Enchantments {

	private List<String> normalEnchantment;
	private List<String> customEnchantment;
	private List<String> skills;
	private List<String> notUpgradable;
	
	private List<String> swordEnchantment;
	private List<String> bowEnchantment;
	
	private List<String> helmetEnchantment;
	private List<String> chestplateEnchantment;
	private List<String> leggingsEnchantment;
	private List<String> bootsEnchantment;

	private List<String> pickaxeEnchantment;
	private List<String> axeEnchantment;
	private List<String> hoeEnchantment;
	private List<String> shovelEnchantment;
	
	public List<String> getNormalEnchant() {
		return normalEnchantment;
	}
	
	public List<String> getCustomEnchant(){
		return customEnchantment;
	}
	
	public List<String> getSkills() {
		return skills;
	}
	
	public List<String> getNotUpgradable() {
		return notUpgradable;
	}
	
	public List<String> getSwordEnchant() {
		return swordEnchantment;
	}
	
	public List<String> getBowEnchant() {
		return bowEnchantment;
	}
	
	public List<String> getHelmetEnchant() {
		return helmetEnchantment;
	}
	
	public List<String> getChestplateEnchant() {
		return chestplateEnchantment;
	}
	
	public List<String> getLeggingsEnchant() {
		return leggingsEnchantment;
	}
	
	public List<String> getBootsEnchant() {
		return bootsEnchantment;
	}
	
	public List<String> getPickaxeEnchant() {
		return pickaxeEnchantment;
	}
	
	public List<String> getAxeEnchant() {
		return axeEnchantment;
	} 
	
	public List<String> getHoeEnchant() {
		return hoeEnchantment;
	}
	
	public List<String> getShovelEnchant() {
		return shovelEnchantment;
	}
	
	public void setAllEnchantment() {
		setNormalEnchants();
		setCustomEnchants();
		setSkills();
		setNotUpgradable();
		setSwordEnchantment();
		setBowEnchantment();
		setHelmentEnchant();
		setChestplateEnchant();
		setLeggingsEnchant();
		setBootsEnchant();
		setPickaxeEnchant();
		setAxeEnchant();
		setHoeEnchant();
		setShovelEnchant();
	}
	
	public void setNormalEnchants() {
		normalEnchantment = new ArrayList<>();
		normalEnchantment.add("Bane of Arthropods".toUpperCase());
		normalEnchantment.add("Blast Protection".toUpperCase());
		normalEnchantment.add("Bane of Arthropods".toUpperCase());
		normalEnchantment.add("Depth Strider".toUpperCase());
		normalEnchantment.add("Efficiency".toUpperCase());
		normalEnchantment.add("Feather Falling".toUpperCase());
		normalEnchantment.add("Fire Protection".toUpperCase());
		normalEnchantment.add("Fortune".toUpperCase());
		normalEnchantment.add("Knockback".toUpperCase());
		normalEnchantment.add("Power".toUpperCase());
		normalEnchantment.add("Projectile Protection".toUpperCase());
		normalEnchantment.add("Protection".toUpperCase());	
		normalEnchantment.add("Punch".toUpperCase());
		normalEnchantment.add("Respiration".toUpperCase());
		normalEnchantment.add("Sharpness".toUpperCase());
		normalEnchantment.add("Smite".toUpperCase());
		normalEnchantment.add("Thorns".toUpperCase());
		normalEnchantment.add("Unbreaking".toUpperCase());
		normalEnchantment.add("Looting".toUpperCase());
	}
	
	public void setCustomEnchants() {
		customEnchantment = new ArrayList<>();
		customEnchantment.add("CRITICAL");
		customEnchantment.add("HEADSHOT");
		customEnchantment.add("SPEED");
		customEnchantment.add("HEALTH BOOST");
		customEnchantment.add("LAST RESORT");
		customEnchantment.add("LIFE STEAL");
		customEnchantment.add("REJUVENATION");
		customEnchantment.add("SOUL BOUND");
	}
	
	public void setSkills() {
		skills = new ArrayList<>();
		skills.add("LIGHTNING STRIKE");
		skills.add("EXPLOSION");
		skills.add("ARISE");
		skills.add("GRAVITY");
		skills.add("RAGE");
		skills.add("FULL COUNTER");
	}
	
	public void setNotUpgradable() {
		notUpgradable = new ArrayList<>();
		notUpgradable.add("SPEED");
		notUpgradable.add("LAST RESORT");
		notUpgradable.add("REJUVENATION");
		notUpgradable.add("GRAVITY");
		notUpgradable.add("SOUL BOUND");
		notUpgradable.add("RAGE");
		notUpgradable.add("FULL COUNTER");
		
	}
	
	public void setSwordEnchantment() {
		swordEnchantment = new ArrayList<>();
		swordEnchantment.add("CRITICAL");
		swordEnchantment.add("LIFE STEAL");
		swordEnchantment.add("SOUL BOUND");
	}
	
	public void setBowEnchantment() {
		bowEnchantment = new ArrayList<>();
		bowEnchantment.add("HEADSHOT");
		bowEnchantment.add("LIGHTNING STRIKE");
		bowEnchantment.add("EXPLOSION");
		bowEnchantment.add("GRAVITY");
		bowEnchantment.add("SOUL BOUND");
	}
	
	public void setHelmentEnchant() {
		helmetEnchantment = new ArrayList<>();
		helmetEnchantment.add("LAST RESORT");
		helmetEnchantment.add("ARISE");
		helmetEnchantment.add("SOUL BOUND");
	}
	
	public void setChestplateEnchant() {
		chestplateEnchantment = new ArrayList<>();
		chestplateEnchantment.add("REJUVENATION");
		chestplateEnchantment.add("HEALTH BOOST");
		chestplateEnchantment.add("SOUL BOUND");
		chestplateEnchantment.add("RAGE");
	}
	
	public void setLeggingsEnchant() {
		leggingsEnchantment = new ArrayList<>();
		leggingsEnchantment.add("SOUL BOUND");
		leggingsEnchantment.add("FULL COUNTER");
	}
	
	public void setBootsEnchant() {
		bootsEnchantment = new ArrayList<>();
		bootsEnchantment.add("SPEED");
		bootsEnchantment.add("SOUL BOUND");
	}
	
	public void setPickaxeEnchant() {
		pickaxeEnchantment = new ArrayList<>();
		pickaxeEnchantment.add("SOUL BOUND");
	}
	
	public void setAxeEnchant() {
		axeEnchantment = new ArrayList<>();
		axeEnchantment.add("LIFE STEAL");
		axeEnchantment.add("CRITICAL");
		axeEnchantment.add("SOUL BOUND");
	}
	
	public void setHoeEnchant() {
		hoeEnchantment = new ArrayList<>();
		hoeEnchantment.add("SOUL BOUND");
	}
	
	public void setShovelEnchant() {
		shovelEnchantment = new ArrayList<>();
		shovelEnchantment.add("SOUL BOUND");
	}
}
