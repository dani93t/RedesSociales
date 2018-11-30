package com.beacons.sociales.redes.daniel.pruebabeacons2;

import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    private ProximityObserver proximityObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EstimoteCloudCredentials cloudCredentials = new EstimoteCloudCredentials("proyectopersonalizado-ipc","0eb649e5b9f629cf85c5bc5f15beb546");


        this.proximityObserver =
                new ProximityObserverBuilder(getApplicationContext(), cloudCredentials)
                        .onError(new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                Log.e("app", "proximity observer error: " + throwable);
                                return null;
                            }
                        })
                        .withBalancedPowerMode()
                        .build();

        final MainActivity mi=this;
        final ProximityZone zone = new ProximityZoneBuilder().forTag("Redes Sociales")
                .inNearRange()
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext context) {  //la funcion se activa por un evento de estimote
                        String deskOwner = context.getAttachments().get("desk-owner");
                        Log.d("app", "Welcome to " + deskOwner + "'s desk");
                        Toast.makeText(mi,"Welcome to " + deskOwner + "'s desk : " + context.toString(),Toast.LENGTH_SHORT).show();
                        return null;
                    }
                })
                .onExit(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext context) {
                        Log.d("app", "Bye bye, come again!");  //la funcion se activa por un evento de estimote
                        Toast.makeText(mi,"Bye bye, come again!",Toast.LENGTH_SHORT).show();
                        return null;
                    }
                })
                .onContextChange(new Function1<Set<? extends ProximityZoneContext>, Unit>() {
                    @Override
                    public Unit invoke(Set<? extends ProximityZoneContext> contexts) {
                        List<String> deskOwners = new ArrayList<>();      //la funcion se activa por un evento de estimote
                        for (ProximityZoneContext context : contexts) {
                            deskOwners.add(context.getAttachments().get("desk-owner"));
                            Toast.makeText(mi,"Cambio contexto",Toast.LENGTH_LONG).show();
                        }
                        Log.d("app", "In range of desks: " + deskOwners);
                        return null;
                    }
                })
                .build();


        ProximityZone innerZone = new ProximityZoneBuilder()
                .forTag("Redes Sociales")
                .inCustomRange(3.0)
                .build();


        ProximityZone outerZone = new ProximityZoneBuilder()
                .forTag("Redes Sociales")
                .inCustomRange(9.0)
                .build();


        RequirementsWizardFactory
                .createEstimoteRequirementsWizard()
                .fulfillRequirements(this,
                        // onRequirementsFulfilled
                        new Function0<Unit>() {
                            @Override public Unit invoke() {
                                Log.d("app", "requirements fulfilled");
                                proximityObserver.startObserving(zone);
                                return null;
                            }
                        },
                        // onRequirementsMissing
                        new Function1<List<? extends Requirement>, Unit>() {
                            @Override public Unit invoke(List<? extends Requirement> requirements) {
                                Log.e("app", "requirements missing: " + requirements);
                                return null;
                            }
                        },
                        // onError
                        new Function1<Throwable, Unit>() {
                            @Override public Unit invoke(Throwable throwable) {
                                Log.e("app", "requirements error: " + throwable);
                                return null;
                            }
                        });



    }







}
