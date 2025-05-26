package Game;

public class TimerManager
{
    private long missileTimer;
    private long enemyMissileTimer;
    private long strayEnemyTimer;
    private long pauseTimer;
    private long initiateNewGameTimer;
    private long pauseCountDownTimer;
    private long showPlayerSensitivityTimer;
    private long playerExplosionTimer;
    private long showExplosionsTimer;
    private long showScoreBubblesTimer;
    private long showSoundSettingTimer;
    public long getTimer(final Timer timerType)
    {
        long time = 0;
        switch(timerType)
        {
            case MISSILE:
                time = missileTimer;
                break;
            case ENEMY_MISSILE:
                time = enemyMissileTimer;
                break;
            case SETTING_CHANGE_MESSAGE:
                time = showPlayerSensitivityTimer;
                break;
            case STRAY_ENEMY:
                time = strayEnemyTimer;
                break;
            case PAUSE:
                time = pauseTimer;
                break;
            case INITIATE_NEW_GAME:
                time = initiateNewGameTimer;
                break;
            case PAUSE_COUNT_DOWN:
                time = pauseCountDownTimer;
                break;
            case PLAYER_EXPLOSION:
                time = playerExplosionTimer;
                break;
            case SHOW_EXPLOSIONS:
                time = showExplosionsTimer;
                break;
            case SHOW_SCORE_BUBBLES:
                time = showScoreBubblesTimer;
                break;
            case SHOW_SOUND_SETTING:
                time = showSoundSettingTimer;
        }
        return time;
    }
    public void resetTimer(final Timer timerType)
    {
        setTimer(timerType, 0);
    }
    public void setTimer(final Timer timerType, final long time)
    {
        switch(timerType)
        {
            case MISSILE:
                missileTimer = time;
                break;
            case ENEMY_MISSILE:
                enemyMissileTimer = time;
                break;
            case SETTING_CHANGE_MESSAGE:
                showPlayerSensitivityTimer = time;
                break;
            case STRAY_ENEMY:
                strayEnemyTimer = time;
                break;
            case PAUSE:
                pauseTimer = time;
                break;
            case INITIATE_NEW_GAME:
                initiateNewGameTimer = time;
                break;
            case PAUSE_COUNT_DOWN:
                pauseCountDownTimer = time;
                break;
            case PLAYER_EXPLOSION:
                playerExplosionTimer = time;
                break;
            case SHOW_EXPLOSIONS:
                showExplosionsTimer = time;
                break;
            case SHOW_SCORE_BUBBLES:
                showScoreBubblesTimer = time;
                break;
            case SHOW_SOUND_SETTING:
                showSoundSettingTimer = time;
        }
    }
    public void setToSystemNanoTime(final Timer timerType)
    {
        setTimer(timerType, System.nanoTime());
    }
    public void updateForPauseTimer()
    {
        missileTimer += System.nanoTime() - pauseTimer;
        enemyMissileTimer += System.nanoTime() - pauseTimer;
        strayEnemyTimer += System.nanoTime() - pauseTimer;
        playerExplosionTimer += System.nanoTime() - pauseTimer;
    }
}