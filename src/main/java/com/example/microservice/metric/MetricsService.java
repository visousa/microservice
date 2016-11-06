package com.example.microservice.metric;

import com.codahale.metrics.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by bruno on 04-11-2016.
 */

@Service
public class MetricsService {

    /**
     * get registry bean to log metric
     */
    @Autowired
    private MetricRegistry registry;

    /**
     * List of all timers
     */
    private HashMap<String, Timer> timers = new HashMap<String, Timer>();

    /**
     * List of all histograms
     */
    private HashMap<String, Histogram> histograms = new HashMap<String, Histogram>();

    /**
     * List of all counters
     */
    private HashMap<String, Counter> counters = new HashMap<String, Counter>();

    /**
     * List of all meters
     */
    private HashMap<String, Meter> meters = new HashMap<String, Meter>();


    /**
     * This method get or create a new timer
     * Is thread safe
     * @param name name of metric as suffix of a getMetricClass() namespace
     * @return the timer for the provided name
     */
    public Timer getTimer(String name){
        if(!timers.containsKey(name)){
            /**
             * acquire lock on timers
             */
            synchronized (timers){
                /**
                 * test again (this is need to guarantee thread-safe
                 */
                if(!timers.containsKey(name)){
                    timers.put(name, registry.timer(name(getClass(), name)));
                }
            }
        }
        return timers.get(name);
    }

    /**
     * This method get or create a new histogram
     * Is thread safe
     * @param name name of metric as suffix of a getMetricClass() namespace
     * @return the histogram for the provided name
     */
    public Histogram getHistogram(String name){
        if(!histograms.containsKey(name)){
            /**
             * acquire lock on histograms
             */
            synchronized (histograms){
                /**
                 * test again (this is need to guarantee thread-safe
                 */
                if(!histograms.containsKey(name)){
                    histograms.put(name, registry.histogram(name(getClass(), name)));
                }
            }
        }
        return histograms.get(name);
    }

    /**
     * This method get or create a new counter
     * Is thread safe
     * @param name name of metric as suffix of a getMetricClass() namespace
     * @return the counter for the provided name
     */

    public Counter getCounter(String name){
        if(!counters.containsKey(name)){
            /**
             * acquire lock on counters
             */
            synchronized (counters){
                /**
                 * test again (this is need to guarantee thread-safe
                 */
                if(!counters.containsKey(name)){
                    counters.put(name, registry.counter(name(getClass(), name)));
                }
            }
        }
        return counters.get(name);
    }

    /**
     * This method get or create a new meter
     * Is thread safe
     * @param name name of metric as suffix of a getMetricClass() namespace
     * @return the meter for the provided name
     */

    public Meter getMeter(String name){
        if(!meters.containsKey(name)){
            /**
             * acquire lock on meters
             */
            synchronized (meters){
                /**
                 * test again (this is need to guarantee thread-safe
                 */
                if(!meters.containsKey(name)){
                    meters.put(name, registry.meter(name(getClass(), name)));
                }
            }
        }
        return meters.get(name);
    }
}
