/**
 *
 * @author 6713230_Natwarit Chuboonsub
 * @author 6713229_Nutthapat Techapornhiran
 * @author 6713235_Teetath Prapasanon
 * @author 6713239_Nitich Uanjityanon
 * @author 6713243_Puttipong Chutipongwanit
 */
package Project3_6713230;

import javax.swing.*;
import java.awt.*;

public class Rock extends JLabel {

    private GameMap map;
    private GameFrame parentFrame;

    private int width = GameConstants.ROCK_WIDTH;
    private int height = GameConstants.ROCK_HEIGHT;
    private int curX;
    private int curY;
    private int vx = 0; // เพิ่ม vx สำหรับการยิง
    private int vy = 0;
    private int gravity = GameConstants.GRAVITY;

    private boolean isDestroyed = false;
    private boolean isParticle = false;
    private long particleTimeEnd = 0; // เวลาสิ้นสุด Particle
    
    private GameSoundEffect rocksmashSFX;

    private GameIcon rockIcon;
    private GameIcon particleIcon;

    public Rock(GameFrame pf, GameMap m, int startX, int startY, int startVX, int startVY) {
        parentFrame = pf;
        this.map = m;
        this.curX = startX;
        this.curY = startY;
        this.vx = startVX;
        this.vy = startVY;


        rockIcon = new GameIcon(GameConstants.FILE_GOLEM_ROCK).resize(width, height);
        particleIcon = new GameIcon(GameConstants.FILE_GOLEM_ROCKPARTICLE).resize(width, height);
        
        rocksmashSFX   = new GameSoundEffect(GameConstants.FILE_ROCK_SMASH_SFX);

        setBounds(curX, curY, width, height);
        setIcon(rockIcon);
    }

    public Rectangle getHitBox() {
        // เมื่อเป็น Particle จะไม่มี HitBox
        if (isParticle) {
            return new Rectangle(0, 0, 0, 0);
        }
        return new Rectangle(curX + 20, curY + 20, width - 40, height - 40); // ลดขนาด HitBox เล็กน้อย
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public boolean isParticle() { // เพิ่ม getter
        return isParticle;
    }

    // เปลี่ยน Rock เป็น Particle
    public void destroyAndParticle() {
        if (isParticle) {
            return;
        }

        isParticle = true;
        vx = 0;
        vy = 0;

        particleTimeEnd = System.currentTimeMillis() + GameConstants.PARTICLE_DURATION_MS;
        if (rocksmashSFX != null) rocksmashSFX.playOnce();
        setIcon(particleIcon); // เปลี่ยนเป็นภาพที่แตกแล้ว
    }

    public void updateLocation() {
        if (isDestroyed) {
            return;
        }

        if (isParticle) {
            if (System.currentTimeMillis() > particleTimeEnd) {
                isDestroyed = true;
            }
            return;
        }

        if (curX < -width || curX > GameConstants.FRAME_WIDTH || curY > GameConstants.FRAME_HEIGHT) {
            isDestroyed = true;
            return;
        }

        // 2. อัพเดตความเร็วและการเคลื่อนที่
        // ถ้าเป็นหินตก (vy มีค่าเริ่มต้น, หรือ vx=0) ให้ใช้แรงโน้มถ่วง
        if (vx == 0) {
            vy += gravity;
        }

        int nextX = curX + vx;
        int nextY = curY + vy;

        // 3. ตรวจสอบการชนกับพื้น (สำหรับหินตกเท่านั้น)
        if (vy >= 0) { // เป็นหินตกและกำลังเคลื่อนที่ลง
            boolean leftFoot = map.isSolid(curX + 5, nextY + height - 25);
            boolean rightFoot = map.isSolid(curX + width - 5, nextY + height - 25);

            if (leftFoot || rightFoot) {
                // ชนพื้น เปลี่ยนเป็น Particle
                int tileRow = (nextY + height) / map.TILE_SIZE;
                curY = (tileRow * map.TILE_SIZE) - height;
                destroyAndParticle();
                setLocation(curX, curY);
                return;
            }
        }

        // 4. อัพเดตตำแหน่ง
        curX = nextX;
        curY = nextY;
        setLocation(curX, curY);
    }
}
