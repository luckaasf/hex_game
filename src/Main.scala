object Main {
  def main(args: Array[String]): Unit = {
    Display.startMessage()
    val (boardSize, board, random, boardStates, humanPlayer, machinePlayer) = UserInput.gameInitialConfig()
    val hexGame: HexGame = HexGame(boardSize)
    hexGame.runGame(board, humanPlayer.color, random, boardStates, humanPlayer, machinePlayer)

  }

}