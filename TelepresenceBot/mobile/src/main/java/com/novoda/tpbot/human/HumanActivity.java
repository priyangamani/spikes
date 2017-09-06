package com.novoda.tpbot.human;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.novoda.support.SelfDestructingMessageView;
import com.novoda.support.SwitchableView;
import com.novoda.tpbot.Direction;
import com.novoda.tpbot.R;
import com.novoda.tpbot.ServiceDeclarationListener;
import com.novoda.tpbot.controls.CommandRepeater;
import com.novoda.tpbot.controls.ControllerListener;
import com.novoda.tpbot.controls.ControllerView;
import com.novoda.tpbot.controls.ServerDeclarationView;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class HumanActivity extends AppCompatActivity implements HumanView, ControllerListener, ServiceDeclarationListener, CommandRepeater.Listener {

    private static final String LAZERS = String.valueOf(Character.toChars(0x1F4A5));

    @Inject
    SelfDestructingMessageView debugView;
    @Inject
    SwitchableView switchableView;
    @Inject
    ServerDeclarationView serverDeclarationView;
    @Inject
    ControllerView controllerView;
    @Inject
    CommandRepeater commandRepeater;
    @Inject
    HumanPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_human);
        AndroidInjection.inject(this);
    }

    @Override
    public void onCommandRepeated(String command) {
        debugView.showTimed(command);
        Direction direction = Direction.from(command);
        presenter.moveIn(direction);
    }

    @Override
    public void onServiceConnected(String serverAddress) {
        debugView.showPermanently(getString(R.string.connecting_ellipsis));
        presenter.startPresenting(serverAddress);
    }

    @Override
    public void onConnect(String message) {
        debugView.showPermanently(getString(R.string.connected));
        switchableView.setDisplayedChild(1);
    }

    @Override
    public void onDisconnect() {
        debugView.showPermanently(getString(R.string.disconnected));
        switchableView.setDisplayedChild(0);
    }

    @Override
    public void onError(String message) {
        debugView.showPermanently(message);
        switchableView.setDisplayedChild(0);
    }

    @Override
    protected void onPause() {
        commandRepeater.stopCurrentRepeatingCommand();
        super.onPause();
    }

    @Override
    protected void onStop() {
        presenter.stopPresenting();
        super.onStop();
    }

    @Override
    public void onDirectionPressed(Direction direction) {
        commandRepeater.startRepeatingCommand(direction.rawDirection());
    }

    @Override
    public void onDirectionReleased(Direction direction) {
        commandRepeater.stopRepeatingCommand(direction.rawDirection());
    }

    @Override
    public void onLazersFired() {
        // no-op for debug
    }

    @Override
    public void onLazersReleased() {
        // no-op for debug
    }
}
