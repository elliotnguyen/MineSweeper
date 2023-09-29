package com.example.demoonschool;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.demoonschool.model.MineSweeper;

import java.util.List;

public class MinesweeperActivity extends AppCompatActivity {
    private Button btnStart;
    private Button btnRestart;
    private Button btnFlag;
    private int NextAvailableId = 65000;
    private boolean flagMode = false;
    private MineSweeper mineSweeper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minesweeper);

        btnRestart = findViewById(R.id.buttonRestart);
        btnRestart.setOnClickListener(view -> {
            EditText editNumRows = findViewById(R.id.editNumRows);
            editNumRows.getText().clear();

            EditText editNumCols = findViewById(R.id.editNumCols);
            editNumCols.getText().clear();

            GridLayout gridLayout = (GridLayout) findViewById(R.id.gridMain);
            gridLayout.removeAllViews();
        });
        btnRestart.setEnabled(false);

        btnFlag = findViewById(R.id.buttonFlagOn);
        btnFlag.setOnClickListener(view -> {
            HandleFlagMode();
        });
        btnFlag.setEnabled(false);

        btnStart = findViewById(R.id.buttonStart);
        btnStart.setOnClickListener(view -> {
            int nRows = getNumberFromEditText(R.id.editNumRows);
            int nCols = getNumberFromEditText(R.id.editNumCols);

            if (nRows <= 0 || nCols <= 0) {
                ShowDialogWarning();
            } else {
                generateGame(nRows, nCols);
                btnFlag.setEnabled(true);
                btnRestart.setEnabled(true);
            }
        });
    }

    private void HandleFlagMode() {
        if (!flagMode) {
            for (int i = 0; i < mineSweeper.getNumberOfRows(); i++) {
                for (int j = 0; j < mineSweeper.getNumberOfColumns(); j++) {
                    if (!mineSweeper.isRevealed(i,j)) {
                        int btnId = 65000 + mineSweeper.getNumberOfColumns() * i + j;
                        Button btn = findViewById(btnId);
                        btn.setBackgroundResource(R.drawable.blankforflag);
                    }
                }
            }
            flagMode = true;
        } else {
            for (int i = 0; i < mineSweeper.getNumberOfRows(); i++) {
                for (int j = 0; j < mineSweeper.getNumberOfColumns(); j++) {
                    if (!mineSweeper.isRevealed(i,j) && !mineSweeper.isFlagOn(i,j)) {
                        int btnId = 65000 + mineSweeper.getNumberOfColumns() * i + j;
                        Button btn = findViewById(btnId);
                        btn.setBackgroundResource(R.drawable.untouched);
                    }
                }
            }
            flagMode = false;
        }
    }

    private void ShowDialogWarning() {
        Dialog dialog = new Dialog(this, R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_layout_warning);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);

        Button btnDialog = dialog.findViewById(R.id.buttonCloseWarning);
        btnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void generateGame(int nRows, int nCols) {
        mineSweeper = new MineSweeper(nRows,nCols);
        loadGame(mineSweeper);
    }

    private void loadGame(MineSweeper mineSweeper) {
        GridLayout gridLayout = (GridLayout) findViewById(R.id.gridMain);
        gridLayout.removeAllViews();
        NextAvailableId = 65000;

        int numberOfRows = mineSweeper.getNumberOfRows();
        gridLayout.setRowCount(numberOfRows);
        int numberOfColumns = mineSweeper.getNumberOfColumns();
        gridLayout.setColumnCount(numberOfColumns);

        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                Button button = new Button(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = (gridLayout.getWidth() / gridLayout.getColumnCount()) - params.rightMargin - params.leftMargin;
                params.height = params.width;

                button.setLayoutParams(params);
                button.setBackgroundResource(R.drawable.untouched);

                button.setId(NextAvailableId);

                int finalI = i;
                int finalJ = j;
                int finalButtonId = NextAvailableId;
                button.setOnClickListener(view -> {
                    if (!flagMode)
                    {
                        HandleAfterClickNonFlagMode(finalI, finalJ, finalButtonId, mineSweeper);
                    } else {
                        HandleAfterClickFlagMode(finalI, finalJ, finalButtonId, mineSweeper);
                    }
                });

                gridLayout.addView(button);

                NextAvailableId++;
            }
        }
    }

    private void HandleAfterClickFlagMode(int finalI, int finalJ, int finalButtonId, MineSweeper mineSweeper) {
        Button button = findViewById(finalButtonId);
        if (!mineSweeper.isFlagOn(finalI, finalJ)) {
            if (mineSweeper.canFlag()) {
                mineSweeper.setFlagOn(finalI, finalJ);
                button.setBackgroundResource(R.drawable.untouchedflag);
            }
        } else {
            mineSweeper.setFlagOff(finalI, finalJ);
            button.setBackgroundResource(R.drawable.blankforflag);
        }
    }

    private void ShowDialogWhenFinished(MineSweeper mineSweeper, boolean lose) {
        for (int i = 0; i < mineSweeper.getNumberOfRows(); i++) {
            for (int j = 0; j < mineSweeper.getNumberOfColumns(); j++) {
                int btnId = 65000 + mineSweeper.getNumberOfColumns() * i + j;
                Button btn = findViewById(btnId);
                if (lose) {
                    if (mineSweeper.isRevealed(i,j)) {
                        if (mineSweeper.isFlagOn(i,j) && !mineSweeper.isMineAt(i,j)) {
                            btn.setBackgroundResource(R.drawable.untouchedflagx);
                        }
                    } else if (mineSweeper.isMineAt(i,j)) {
                        btn.setBackgroundResource(R.drawable.bomb);
                    }
                } else {
                    if (!mineSweeper.isRevealed(i,j)) {
                        btn.setBackgroundResource(R.drawable.untouchedflag);
                    }
                }
                btn.setEnabled(false);
            }
        }

        Dialog dialog = new Dialog(this, R.style.DialogStyle);
        dialog.setContentView(R.layout.dialog_layout_lose);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);

        TextView textView = dialog.findViewById(R.id.message);
        if (lose) {
            textView.setText(R.string.game_over);
        } else {
            textView.setText(R.string.congratulation);
        }

        Button btnCloseDialog = dialog.findViewById(R.id.buttonClose);
        btnCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Button btnRetryDialog = dialog.findViewById(R.id.buttonTryAgain);
        btnRetryDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                generateGame(mineSweeper.getNumberOfRows(), mineSweeper.getNumberOfColumns());
            }
        });

        dialog.show();
    }

    private void HandleAfterClickNonFlagMode(int row, int col, int buttonId, MineSweeper mineSweeper) {
        //Button button = findViewById(buttonId);
        if (mineSweeper.isMineAt(row,col)) {
            Button button = findViewById(buttonId);
            button.setBackgroundResource(R.drawable.bombfirstclick);
            button.setEnabled(false);
            mineSweeper.revealCell(row,col);

            ShowDialogWhenFinished(mineSweeper, true);
        } else {
            List<int[]> cellsToBeFilled = mineSweeper.floodFill(row, col);
            if (cellsToBeFilled.size() == 0) return;
            for (int[] cell : cellsToBeFilled) {
                int btnId = 65000 + mineSweeper.getNumberOfColumns() * cell[0] + cell[1];
                Button btn = findViewById(btnId);

                int surroundingMines = mineSweeper.getSurroundingMinesCount(cell[0], cell[1]);
                switch (surroundingMines) {
                    case 1:
                        btn.setBackgroundResource(R.drawable.one);
                        break;
                    case 2:
                        btn.setBackgroundResource(R.drawable.two);
                        break;
                    case 3:
                        btn.setBackgroundResource(R.drawable.three);
                        break;
                    case 4:
                        btn.setBackgroundResource(R.drawable.four);
                        break;
                    case 5:
                        btn.setBackgroundResource(R.drawable.five);
                        break;
                    case 6:
                        btn.setBackgroundResource(R.drawable.six);
                        break;
                    case 7:
                        btn.setBackgroundResource(R.drawable.seven);
                        break;
                    case 8:
                        btn.setBackgroundResource(R.drawable.eight);
                        break;
                    default:
                        btn.setBackgroundResource(R.drawable.blank);
                }

                btn.setEnabled(false);
            }

            if (mineSweeper.isWinner()) {
                ShowDialogWhenFinished(mineSweeper, false);
            }
        }
    }

    private int getNumberFromEditText(int idEditText) {
        EditText editText = (EditText) findViewById(idEditText);
        String sText = editText.getText().toString();
        if (sText.equals("")) return -1;
        int res = Integer.valueOf(sText);
        return res;
    }
}