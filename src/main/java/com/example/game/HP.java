package com.example.game;

public class HP
{
	private int hp;

	public HP(int baseHp)
	{
		this.hp = baseHp;
	}

	public void changeHp(int amount)
	{
		hp += amount;
	}

	public void setHp(int hp)
	{
		this.hp = hp;
	}

	public int getHp()
	{
		return hp;
	}
}
