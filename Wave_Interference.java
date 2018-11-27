import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/*
Purpose: Runnable stand alone version of Wave_Interference
Programmer: Gabriel Toban Harris
Date: 2017-5-13/2017-5-17
*/

public class Wave_Interference extends Application implements EventHandler<ActionEvent>
{
        private double amplitude, wave_number, angular_frequancy, phase_constant;
        private static double x1, t = 0, n = -1;//control variables for animation
        private final static double HORIZONTAL_SCREEN_BOUNDS = Screen.getPrimary().getVisualBounds().getWidth(), VERTICAL_SCREEN_BOUNDS = Screen.getPrimary().getVisualBounds().getHeight(),
                                    HALF_PANE_1_HEIGHT = 3 * VERTICAL_SCREEN_BOUNDS / 14.0;
        private double animation_wave_parameters[] = new double [11];//for waves store y position
        private final static String WAVE_COLOR_STYLE[] = {"#007EE6", "#17E09C", "#8A32FF", "#B5009A", "#8E0900", "#FE6F00", "#E7A300", "#00B537", "#868E00", "#FF0000"};
        private final static String wave_stats_style[] = {"-fx-font: 32 arial;" + " -fx-text-fill: " + WAVE_COLOR_STYLE[0] + ";", "-fx-font: 32 arial;" + " -fx-text-fill: " + WAVE_COLOR_STYLE[1] + ";", "-fx-font: 32 arial;" + " -fx-text-fill: " + WAVE_COLOR_STYLE[2] + ";",
                                                          "-fx-font: 32 arial;" + " -fx-text-fill: " + WAVE_COLOR_STYLE[3] + ";", "-fx-font: 32 arial;" + " -fx-text-fill: " + WAVE_COLOR_STYLE[4] + ";", "-fx-font: 32 arial;" + " -fx-text-fill: " + WAVE_COLOR_STYLE[5] + ";",
                                                          "-fx-font: 32 arial;" + " -fx-text-fill: " + WAVE_COLOR_STYLE[6] + ";", "-fx-font: 32 arial;" + " -fx-text-fill: " + WAVE_COLOR_STYLE[7] + ";", "-fx-font: 32 arial;" + " -fx-text-fill: " + WAVE_COLOR_STYLE[8] + ";",
                                                          "-fx-font: 32 arial;" + " -fx-text-fill: " + WAVE_COLOR_STYLE[9] + ";"};
        private static Wave_Interference wave_container[] = {new Wave_Interference(), new Wave_Interference(), new Wave_Interference(), new Wave_Interference(), new Wave_Interference(),
                                                             new Wave_Interference(), new Wave_Interference(), new Wave_Interference(), new Wave_Interference(), new Wave_Interference()};//default values
        private static Label wave_label_container[] = new Label [11];
        private static ComboBox<Integer> select_wave_combo_box;
        private static CheckBox check_box_container[] = new CheckBox [11];
        private static Slider amplitude_slider, wave_number_slider, angular_frequancy_slider, phase_constant_slider;
        private static Button start_button = new Button("Start"), stop_button = new Button("Stop"),
                              reset_button = new Button("Reset"), help_button = new Button("Help");
        private static ToggleButton demo_button = new ToggleButton("Demo");
        private static HBox other_HBox, label_slider_HBox, slider_HBox, label_check_box_HBox = new HBox(), check_box_HBox = new HBox();
        private static VBox sub_window_2_VBox;
        private static Pane sub_window_1_pane;
        private static TabPane sub_window_3_pane = new TabPane();
        private static Tab tab_container[] = new Tab [11];
        private static Stage stage_container[] = new Stage[4];

        public void start(Stage primaryStage)
        {
         for (byte index = 0; index < stage_container.length; index++)
             stage_container[index] = new Stage();

         //Sub window 2: UI
                //other stuff
         start_button.setOnAction(this);
         stop_button.setOnAction(this);
         reset_button.setOnAction(this);
         help_button.setOnAction(this);
         demo_button.setOnAction(this);
         disenable_buttons(true);
                //combo box
         select_wave_combo_box = setup_combo_box(select_wave_combo_box, "Wave", 6, 134);
         select_wave_combo_box.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
         other_HBox = new HBox(start_button, stop_button, reset_button, help_button, demo_button, select_wave_combo_box);
                //sliders
         amplitude_slider = setup_slider(amplitude_slider, 0, 100, 0, Orientation.HORIZONTAL, false, true, true, 100, 10, 0.5);
         wave_number_slider = setup_slider(wave_number_slider, 0, 2, 1, Orientation.HORIZONTAL, false, true, true, 0.5, 0, 1.0 / 1000);
         angular_frequancy_slider = setup_slider(angular_frequancy_slider, 0, 2, 1, Orientation.HORIZONTAL, false, true, true, 0.5, 0, 1.0 / 1000);
         phase_constant_slider = setup_slider(phase_constant_slider, 0, 2 * Math.PI, 0, Orientation.HORIZONTAL, false, true, true, Math.PI / 4, 0, Math.PI / 10);
                //slider functionality
         amplitude_slider.valueProperty().addListener(new ChangeListener<Number>()
         {
          public void changed(ObservableValue<? extends Number> ovservable, Number old_value, Number new_value)
          {
           update_wave_parameter(1, new_value.doubleValue());
          }
         });
         wave_number_slider.valueProperty().addListener(new ChangeListener<Number>()
         {
          public void changed(ObservableValue<? extends Number> ovservable, Number old_value, Number new_value)
          {
           update_wave_parameter(2, new_value.doubleValue());
          }
         });
         angular_frequancy_slider.valueProperty().addListener(new ChangeListener<Number>()
         {
          public void changed(ObservableValue<? extends Number> ovservable, Number old_value, Number new_value)
          {
           update_wave_parameter(3, new_value.doubleValue());
          }
         });
         phase_constant_slider.valueProperty().addListener(new ChangeListener<Number>()
         {
          public void changed(ObservableValue<? extends Number> ovservable, Number old_value, Number new_value)
          {
           update_wave_parameter(4, new_value.doubleValue());
          }
         });

         label_slider_HBox = new HBox(new Label("Amplitude        "), new Label("Wave Number   "), new Label("Angular Frequancy"), new Label("Phase Constant"));
         slider_HBox = new HBox(amplitude_slider, wave_number_slider, angular_frequancy_slider, phase_constant_slider);
         for (byte index = 1; index < 11; index++)
             label_check_box_HBox.getChildren().add(new Label("Wave " + index));
         label_check_box_HBox.getChildren().add(new Label("Resultant Wave"));
                //check boxes
         for (byte index = 0; index < check_box_container.length; index++)
            {
             check_box_container[index] = setup_check_box(check_box_container[index]);
             check_box_HBox.getChildren().add(check_box_container[index]);
            }
         label_slider_HBox.setSpacing(70);
         slider_HBox.setSpacing(10);
         label_check_box_HBox.setSpacing(5);
         check_box_HBox.setSpacing(25);

         sub_window_2_VBox = new VBox(other_HBox, label_slider_HBox, slider_HBox, label_check_box_HBox, check_box_HBox);
         stage_container[1].setX(0);
         stage_container[1].setY(VERTICAL_SCREEN_BOUNDS * 3 / 7);
         stage_container[1].setScene(new Scene(new BorderPane(sub_window_2_VBox), HORIZONTAL_SCREEN_BOUNDS / 2, VERTICAL_SCREEN_BOUNDS * 3 / 7));

         //Sub window 1: animation
         stage_container[0].setX(0);
         stage_container[0].setY(0);
         stage_container[0].setScene(new Scene(createDrawing(), HORIZONTAL_SCREEN_BOUNDS, VERTICAL_SCREEN_BOUNDS * 3 / 7));

         //Sub window 3: information
                //tabs
         wave_label_container[0] = new Label("y(x, t) = A sin(Kx \u00B1 \u03C9t + \u03A6)\nK = 2\u03C0/\u03BB\nv = \u03C9/K= \u03BB/T\n\u03C9 = 2\u03C0*f = 2\u03C0/T");
         tab_container[0] = new Tab("Formulas");
         wave_label_container[0].setStyle("-fx-font: 32 arial;");
         tab_container[0].setContent(wave_label_container[0]);
         tab_container[0].setClosable(false);
         sub_window_3_pane.getTabs().add(tab_container[0]);
         for (byte index = 1; index < tab_container.length; index++)
            {
             //-1 for index conversion
             wave_label_container[index] = new Label(calculate_wave_stats(index - 1));
             wave_label_container[index].setStyle(wave_stats_style[index - 1]);
             tab_container[index] = new Tab("Wave " + index);
             tab_container[index].setContent(wave_label_container[index]);
             tab_container[index].setClosable(false);
             sub_window_3_pane.getTabs().add(tab_container[index]);
            }
         stage_container[2].setX(HORIZONTAL_SCREEN_BOUNDS / 2);
         stage_container[2].setY(VERTICAL_SCREEN_BOUNDS * 3 / 7);
         stage_container[2].setScene(new Scene(new BorderPane(sub_window_3_pane), HORIZONTAL_SCREEN_BOUNDS / 2, VERTICAL_SCREEN_BOUNDS * 3 / 7));
         //phi upper case = \u03A6
         //pi lower case = \u03C0
         //lambda lower case = \u03BB
         //omega lower case = \u03C9
         //+/- = \u00B1

         //Sub window 4: help
         stage_container[3].setX(0);
         stage_container[3].setY(VERTICAL_SCREEN_BOUNDS * 6 / 7);
         stage_container[3].setScene(new Scene(new BorderPane(), HORIZONTAL_SCREEN_BOUNDS, VERTICAL_SCREEN_BOUNDS / 7));

         //for loop to finish stages
         for (byte index = 0; index < stage_container.length; index++)
            {
             stage_container[index].setResizable(false);
             stage_container[index].initStyle(StageStyle.UNDECORATED);
             stage_container[index].setOnCloseRequest(new EventHandler<WindowEvent>(){public void handle(WindowEvent we){close_everything(stage_container);}});
             stage_container[index].setTitle("Sub Window " + (index + 1));
             stage_container[index].show();
            }
        }
        //animates animation
        private AnimationTimer animation = new AnimationTimer()
        {
         public void handle(long now)
         {
          t++;
          stage_container[0].setScene(new Scene(createDrawing(), HORIZONTAL_SCREEN_BOUNDS, VERTICAL_SCREEN_BOUNDS * 3 / 7));
         }
        };

        public void handle(ActionEvent e)
        {
         Object source = e.getSource();
         //resets animation
         stage_container[0].setScene(new Scene(createDrawing(), HORIZONTAL_SCREEN_BOUNDS, VERTICAL_SCREEN_BOUNDS * 3 / 7));
         if (start_button == source)
           {
            disenable_buttons(false);
            animation.start();
           }
         else if (stop_button == source)
             {
              disenable_buttons(true);
              animation.stop();
             }
         else if (reset_button == source)
             {
              //reset everything
              for (byte index = 1; index < wave_label_container.length; index++)
                 {
                  wave_container[index - 1] = new Wave_Interference();
                  wave_label_container[index].setText(calculate_wave_stats(index - 1));
                  tab_container[index].setContent(wave_label_container[index]);
                 }
              for (byte index = 0; index < check_box_container.length; index++)
                  check_box_container[index].setSelected(true);
              start_button.setDisable(false);
              stop_button.setDisable(true);
              t = 0;
              amplitude_slider.setValue(0);
              wave_number_slider.setValue(1);
              angular_frequancy_slider.setValue(1);
              phase_constant_slider.setValue(0);
              demo_button.setSelected(false);
              disenable_buttons(true);
              disenable_sliders(false);
              select_wave_combo_box.setValue(null);
              animation.stop();
              stage_container[0].setScene(new Scene(createDrawing(), HORIZONTAL_SCREEN_BOUNDS, VERTICAL_SCREEN_BOUNDS * 3 / 7));
             }
         else if (help_button == source)
             {
              VBox helpText = new VBox(new Text("This animation demonstrates multiwave interference. The animation's performance is portional to the number of visible waves. Waves are visible if their corresponding check box is selected." +
                                                "\n\n The data of the waves shown in Sub Window 3, corresponds in a colour coded manner to the waves in the animation displayed in Sub Window 1." +
                                                "\n\n To utilize animation, move the sliders to alter the data of the object that is selected through the combo box." +
                                                "\n\n The \"Start\" button starts the animation, the \"Stop\" button stops the animation, the  \"Reset\" button resets the pieces to their defult postions, the \"Help\" button displays this information, and \"Demo\" toggle button shows a Fouier Series Square Wave."));
              helpText.setAlignment(Pos.TOP_CENTER);
              stage_container[3].setScene(new Scene(new BorderPane(helpText), HORIZONTAL_SCREEN_BOUNDS, VERTICAL_SCREEN_BOUNDS / 7));
              help_button.setDisable(true);
             }
         else if (demo_button == source)
             {
              disenable_sliders(demo_button.isSelected());
              stage_container[0].setScene(new Scene(createDrawing(), HORIZONTAL_SCREEN_BOUNDS, VERTICAL_SCREEN_BOUNDS * 3 / 7));
             }
        }


        //update selected wave
        private void update_wave_parameter(int parameter, double value)
        {
         //in case nothing selected
         if (select_wave_combo_box.getValue() == null)
            select_wave_combo_box.setValue(1);
         //-1 for index conversion
         switch (parameter)
               {
                case 1: wave_container[select_wave_combo_box.getValue() - 1].set_amplitude(value);
                break;
                case 2: wave_container[select_wave_combo_box.getValue() - 1].set_wave_number(value);
                break;
                case 3: wave_container[select_wave_combo_box.getValue() - 1].set_angular_frequancy(value);
                break;
                case 4: wave_container[select_wave_combo_box.getValue() - 1].set_phase_constant(value);
                break;
                default: throw new Error("Switch statement impossible case");
               }
         //Task for updating wave_label_container[] and by extension tab_container[]
         Task<Void> task_label = new Task<Void>()
         {
          public Void call() throws Exception
          {
           updateMessage(calculate_wave_stats(select_wave_combo_box.getValue() - 1));
           return null;
          }
         };
         task_label.messageProperty().addListener((obs, oldMessage, newMessage) -> wave_label_container[select_wave_combo_box.getValue()].setText(newMessage));
         Thread updating_label_thread = new Thread(task_label);
         updating_label_thread.setDaemon(true);
         updating_label_thread.start();
        }

        //calculate wave stats, formulas for traveling waves
        private String calculate_wave_stats(int index)
        {
         return "Amplitude: " + wave_container[index].get_amplitude() + "\nWave Number: " + wave_container[index].get_wave_number() + "\nAngular Frequency: " + wave_container[index].get_angular_frequancy() + "\nPhase Constant: " + wave_container[index].get_phase_constant() +
                "\nWave Length: " + (2 * Math.PI)/wave_container[index].get_wave_number() + "\nFrequency: " + wave_container[index].get_angular_frequancy() / (2 * Math.PI) + "\nPeriod: " + (2 * Math.PI) / wave_container[index].get_angular_frequancy() + "\nSpeed of Wave: " + wave_container[index].get_angular_frequancy() / wave_container[index].get_wave_number();
        }

        //calculate y(x, t) = A sin(-\u03C9t + Kx  + \u03A6) subroutine, change values
        private double y_position(byte wave, double x, double t)
        {
         n += 2;
         //Fourier Series Square Wave for demo_button
         if (demo_button.isSelected())
            return (HALF_PANE_1_HEIGHT - 40) * (4.0 / Math.PI) * Math.sin((n * Math.PI * (x - t)) / (HORIZONTAL_SCREEN_BOUNDS / 2.0)) / n;
         //In case of INF and to avoid false +
         if (wave_container[wave].get_wave_number() == 0 || wave_container[wave].get_amplitude() == 0)
            return 0;
         return wave_container[wave].get_amplitude() * Math.sin(wave_container[wave].get_wave_number() * x - wave_container[wave].get_angular_frequancy() * t + wave_container[wave].get_phase_constant());
        }

        //set up check boxes
        private CheckBox setup_check_box(CheckBox setup)
        {
         setup = new CheckBox();
         setup.setSelected(true);
         setup.setOnAction(this);
         return setup;
        }

        //set up slider
        private static Slider setup_slider(Slider setup, double min, double max, double starting, Orientation orientation, boolean snap, boolean tick_labels,  boolean tick_marcks, double major_ticks, int minor_ticks, double slide)
        {
         setup = new Slider(min, max, starting);
         setup.setOrientation(orientation);
         setup.setSnapToTicks(snap);
         setup.setShowTickLabels(tick_labels);
         setup.setShowTickMarks(tick_marcks);
         setup.setMajorTickUnit(major_ticks);
         setup.setMinorTickCount(minor_ticks);
         setup.setBlockIncrement(slide);
         return setup;
        }

        //combo box setup
        private static <G> ComboBox<G> setup_combo_box(ComboBox<G> setup, String prompt, int row, double min_width)
        {
         setup = new ComboBox<G>();
         setup.setPromptText(prompt);
         setup.setVisibleRowCount(row);
         setup.setMinWidth(min_width);
         return setup;
        }

        //dis/en-ables buttons
        private void disenable_buttons(boolean buttons)
        {
         start_button.setDisable(!buttons);
         stop_button.setDisable(buttons);
        }

        //dis/en-ables sliders
        private void disenable_sliders(boolean sliders)
        {
         amplitude_slider.setDisable(sliders);
         wave_number_slider.setDisable(sliders);
         angular_frequancy_slider.setDisable(sliders);
         phase_constant_slider.setDisable(sliders);
        }

        //close all stages
        private void close_everything(Stage[] stages_close)
        {
         for (byte index = 0; index < stages_close.length; index++)
             stages_close[index].close();
         System.exit(0);
        }

        //constructors
        public Wave_Interference()
        {
         this.amplitude = 0;
         this.wave_number = 1;
         this.angular_frequancy = 1;
         this.phase_constant = 0;
        }

        //getters
        private double get_amplitude()
        {
         return amplitude;
        }
        private double get_wave_number()
        {
         return wave_number;
        }
        private double get_angular_frequancy()
        {
         return angular_frequancy;
        }
        private double get_phase_constant()
        {
         return phase_constant;
        }
        //setters
        private void set_amplitude(double amplitude)
        {
         this.amplitude = amplitude;
        }
        private void set_wave_number(double wave_number)
        {
         this.wave_number = wave_number;
        }
        private void set_angular_frequancy(double angular_frequancy)
        {
         this.angular_frequancy = angular_frequancy;
        }
        private void set_phase_constant(double phase_constant)
        {
         this.phase_constant = phase_constant;
        }

        public Pane createDrawing()
        {
         sub_window_1_pane = new Pane();
         //draw whole animation
         //default/reset values
         x1 = 0;
         //for drawing
         Polyline resultant_wave = new Polyline();
         Polyline traveling_waves[] = {new Polyline(), new Polyline(), new Polyline(), new Polyline(), new Polyline(),
                                       new Polyline(), new Polyline(), new Polyline(), new Polyline(), new Polyline()};
         //draws stuff
         while (x1 < HORIZONTAL_SCREEN_BOUNDS)
              {
               //default/reset values
               n = -1;
               animation_wave_parameters[10] = 0;
               for (byte index = 0; index < animation_wave_parameters.length - 1; index++)
                  {
                   //y(x, t) position
                   if (check_box_container[index].isSelected())
                     {
                      animation_wave_parameters[index] = y_position(index, x1, t);
                      animation_wave_parameters[10] += animation_wave_parameters[index];
                     }
                  }
               x1 += 0.5;
               //draw traveling waves
               for (byte index = 0; index < traveling_waves.length ;index++)
                  {
                   if (check_box_container[index].isSelected())
                     {
                      traveling_waves[index].getPoints().addAll(new Double[]{x1, HALF_PANE_1_HEIGHT - animation_wave_parameters[index]});
                      traveling_waves[index].setStyle("-fx-stroke: " + WAVE_COLOR_STYLE[index] + ";");
                     }
                  }
               //draw resultant wave
               if (check_box_container[10].isSelected())
                 {
                  resultant_wave.getPoints().addAll(new Double[]{x1, HALF_PANE_1_HEIGHT - animation_wave_parameters[10]});
                  //draw circles
                  if (x1 % 12.0 == 0)
                     sub_window_1_pane.getChildren().add(new Circle(x1 - 1.5, HALF_PANE_1_HEIGHT - animation_wave_parameters[10] - 1.5, 3));
                 }
              }
         //adds stuff
         for (byte index = 0; index < traveling_waves.length; index++)
             if (check_box_container[index].isSelected())
                sub_window_1_pane.getChildren().add(traveling_waves[index]);
         //resultant wave and axis
         sub_window_1_pane.getChildren().addAll(resultant_wave, new Line(0, HALF_PANE_1_HEIGHT, HORIZONTAL_SCREEN_BOUNDS, HALF_PANE_1_HEIGHT));

         return new BorderPane(sub_window_1_pane);
        }

        public static void main(String[] args) {launch(args);}
}
