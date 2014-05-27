package lp.german.buissultimo.app;

/**
 * Created by paullichtenberger on 09.05.14.
 */
public class Match {

    private boolean bIsMatchFinished;
    private int nIdTeam1, nIdTeam2, nPointsTeam1, nPointsTeam2;
    private String sNameTeam1, sNameTeam2;
    private int nWinningTeamId;

    public Match(boolean bIsMatchFinished, int nIdTeam1, int nIdTeam2, int nPointsTeam1, int nPointsTeam2, String sNameTeam1, String sNameTeam2){
        this.bIsMatchFinished = bIsMatchFinished;
        this.nIdTeam1 = nIdTeam1;
        this.nIdTeam2 = nIdTeam2;
        this.sNameTeam1 = sNameTeam1;
        this.sNameTeam2 = sNameTeam2;
        this.nPointsTeam1 = nPointsTeam1;
        this.nPointsTeam2 = nPointsTeam2;

        if(nPointsTeam1>nPointsTeam2){
            nWinningTeamId = nIdTeam1;
        }else if(nPointsTeam1==nPointsTeam2){
            nWinningTeamId = 666;
        }else {
            nWinningTeamId = nIdTeam2;
        }
    }

    public boolean isMatchFinished(){
        return bIsMatchFinished;
    }

    public int getIdTeam1(){
        return nIdTeam1;
    }

    public int getIdTeam2(){
        return nIdTeam2;
    }

    public int getPointsTeam1(){
        return nPointsTeam1;
    }

    public int getPointsTeam2(){
        return nPointsTeam2;
    }

    public String getNameTeam1(){
        return sNameTeam1;
    }

    public String getNameTeam2(){
        return sNameTeam2;
    }

    public int getWinningTeamId() {
        return nWinningTeamId;
    }
}
