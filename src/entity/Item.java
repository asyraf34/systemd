package entity;

import java.awt.Color;

import engine.DrawManager.SpriteType;

import engine.GameState;
import engine.ItemManager.ItemType;


/**
 * Implements Item that moves vertically down.
 */
public class Item extends Entity {

    // Type of Item
    private ItemType type;

    // Item Movement Speed
    private int itemSpeed;

    /**
     * Constructor, establishes the Item's properties.
     *
     * @param itemType
     *            Type of Item being spawned
     *
     * @param spriteType
     *            Sprite of itemType
     *
     * @param positionX
     *            Initial position of the Item in the X axis.
     * @param positionY
     *            Initial position of the Item in the Y axis.
     * @param speed
     *            Speed of the Item, positive or negative depending on
     *            direction - positive is down.
     */

    public Item(ItemType itemType, SpriteType spriteType,
                final int positionX, final int positionY, final int speed) {

        super(positionX, positionY, 3 * 2, 5 * 2, Color.WHITE);

        this.type = itemType;
        this.spriteType = spriteType;
        this.itemSpeed = speed;

        setSprite();

    }

     public final void setSprite() {
     this.spriteType = type.spriteType;
     }


    /**
     * Updates the Item's position.
     */
    public final void update() {
        this.positionY += this.itemSpeed;
    }

    public void applyEffect() {
    }
    /** Apply the Item's effect. */
    public void applyEffect(final Ship ship, final GameState state){
        if (this.type == engine.ItemManager.ItemType.COIN) {
            final int coinAmount = 10; // coin item value
            if (ship != null && state != null) {

                int playerIndex = ship.getPlayerId() - 1;
                if (playerIndex >= 0 && playerIndex < GameState.NUM_PLAYERS) {
                    state.addCoins(playerIndex, coinAmount);
                }
            }
        } else {
            // heal and score item can be implemented here.
        }
    }


    /**
     * Setter of the speed of the Item.
     *
     * @param itemSpeed
     *            New speed of the Item.
     */
    public final void setItemSpeed(final int itemSpeed) {
        this.itemSpeed = itemSpeed;
    }

    public final int getItemSpeed() {
        return this.itemSpeed;
    }

    public final void reset(ItemType newType) {
        this.type = newType;
        this.itemSpeed = 0;
        setSprite(); // change to your enum if different
    }

    /**
     * Getter for the speed of the Item.
     *
     * @return Speed of the Item.
     */
    public final ItemType getType() {
        return this.type;
    }


}
