/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package mmdeploy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Pair;
import android.util.TypedValue;

import java.util.LinkedList;
import java.util.List;

import mmdeploy.widget.BorderedText;


public class MultiBoxTracker {
  private static final float TEXT_SIZE_DIP = 18;
  private static final int[] COLORS = {
    Color.BLUE,
    Color.RED,
    Color.GREEN,
    Color.YELLOW,
    Color.CYAN,
    Color.MAGENTA,
    Color.WHITE,
    Color.parseColor("#55FF55"),
    Color.parseColor("#FFA500"),
    Color.parseColor("#FF8888"),
    Color.parseColor("#AAAAFF"),
    Color.parseColor("#FFFFAA"),
    Color.parseColor("#55AAAA"),
    Color.parseColor("#AA33AA"),
    Color.parseColor("#0D0068")
  };

  //  private final String [] CLASSNAME = {"person", "bicycle", "car", "motorcycle", "airplane", "bus", "train", "truck", "boat", "traffic light",
//          "fire hydrant", "stop sign", "parking meter", "bench", "bird", "cat", "dog", "horse", "sheep", "cow",
//          "elephant", "bear", "zebra", "giraffe", "backpack", "umbrella", "handbag", "tie", "suitcase", "frisbee",
//          "skis", "snowboard", "sports ball", "kite", "baseball bat", "baseball glove", "skateboard", "surfboard",
//          "tennis racket", "bottle", "wine glass", "cup", "fork", "knife", "spoon", "bowl", "banana", "apple",
//          "sandwich", "orange", "broccoli", "carrot", "hot dog", "pizza", "donut", "cake", "chair", "couch",
//          "potted plant", "bed", "dining table", "toilet", "tv", "laptop", "mouse", "remote", "keyboard", "cell phone",
//          "microwave", "oven", "toaster", "sink", "refrigerator", "book", "clock", "vase", "scissors", "teddy bear",
//          "hair drier", "toothbrush"};
  public static final String [] CLASSNAME = {
          "LAMY","tumi","warrior","sandisk","belle","ThinkPad","rolex","balabala","vlone","nanfu","KTM","VW","libai","snoopy",
          "Budweiser","armani","gree","GOON","KielJamesPatrick","uniqlo","peppapig","valentino","GUND","christianlouboutin",
          "toyota","moutai","semir","marcjacobs","esteelauder","chaoneng","goldsgym","airjordan","bally","fsa","jaegerlecoultre",
          "dior","samsung","fila","hellokitty","Jansport","barbie","VDL","manchesterunited","coach","PopSockets","haier",
          "banbao","omron","fendi","erke","lachapelle","chromehearts","leader","pantene","motorhead","girdear","fresh",
          "katespade","pandora","Aape","edwin","yonghui","Levistag","kboxing","yili","ugg","CommedesGarcons","Bosch",
          "palmangels","razer","guerlain","balenciaga","anta","Duke","kingston","nestle","FGN","vrbox","toryburch",
          "teenagemutantninjaturtles","converse","nanjiren","Josiny","kappa","nanoblock","lincoln","michael_kors",
          "skyworth","olay","cocacola","swarovski","joeone","lining","joyong","tudor","YEARCON","hyundai","OPPO",
          "ralphlauren","keds","amass","thenorthface","qingyang","mujosh","baishiwul","dissona","honda","newera","brabus","hera","titoni","decathlon","DanielWellington","moony","etam","liquidpalisade","zippo","mistine","eland","wodemeiliriji","ecco","xtep","piaget","gloria","hp","loewe","Levis_AE","Anna_sui","MURATA","durex","zebra","kanahei","ihengima","basichouse","hla","ochirly","chloe","miumiu","aokang","SUPERME","simon","bosideng","brioni","moschino","jimmychoo","adidas","lanyueliang","aux","furla","parker","wechat","emiliopucci","bmw","monsterenergy","Montblanc","castrol","HUGGIES","bull","zhoudafu","leaders","tata","oldnavy","OTC","levis","veromoda","Jmsolution","triangle","Specialized","tries","pinarello","Aquabeads","deli","mentholatum","molsion","tiffany","moco","SANDVIK","franckmuller","oakley","bulgari","montblanc","beaba","nba","shelian","puma","PawPatrol","offwhite","baishiwuliu","lexus","cainiaoguoguo","hugoboss","FivePlus","shiseido","abercrombiefitch","rejoice","mac","chigo","pepsicola","versacetag","nikon","TOUS","huawei","chowtaiseng","Amii","jnby","jackjones","THINKINGPUTTY","bose","xiaomi","moussy","Miss_sixty","Stussy","stanley","loreal","dhc","sulwhasoo","gentlemonster","midea","beijingweishi","mlb","cree","dove","PJmasks","reddragonfly","emerson","lovemoschino","suzuki","erdos","seiko","cpb","royalstar","thehistoryofwhoo","otterbox","disney","lindafarrow","PATAGONIA","seven7","ford","bandai","newbalance","alibaba","sergiorossi","lacoste","bear","opple","walmart","clinique","asus","ThomasFriends","wanda","lenovo","metallica","stuartweitzman","karenwalker","celine","miui","montagut","pampers","darlie","toray","bobdog","ck","flyco","alexandermcqueen","shaxuan","prada","miiow","inman","3t","gap","Yamaha","fjallraven","vancleefarpels","acne","audi","hunanweishi","henkel","mg","sony","CHAMPION","iwc","lv","dolcegabbana","avene","longchamp","anessa","satchi","hotwheels","nike","hermes","jiaodan","siemens","Goodbaby","innisfree","Thrasher","kans","kenzo","juicycouture","evisu","volcom","CanadaGoose","Dickies","angrybirds","eddrac","asics","doraemon","hisense","juzui","samsonite","hikvision","naturerepublic","Herschel","MANGO","diesel","hotwind","intel","arsenal","rayban","tommyhilfiger","ELLE","stdupont","ports","KOHLER","thombrowne","mobil","Belif","anello","zhoushengsheng","d_wolves","FridaKahlo","citizen","fortnite","beautyBlender","alexanderwang","charles_keith","panerai","lux","beats","Y-3","mansurgavriel","goyard","eral","OralB","markfairwhale","burberry","uno","okamoto","only","bvlgari","heronpreston","jimmythebull","dyson","kipling","jeanrichard","PXG","pinkfong","Versace","CCTV","paulfrank","lanvin","vans","cdgplay","baojianshipin","rapha","tissot","casio","patekphilippe","tsingtao","guess","Lululemon","hollister","dell","supor","MaxMara","metersbonwe","jeanswest","lancome","lee","omega","lets_slim","snp","PINKFLOYD","cartier","zenith","LG","monchichi","hublot","benz","apple","blackberry","wuliangye","porsche","bottegaveneta","instantlyageless","christopher_kane","bolon","tencent","dkny","aptamil","makeupforever","kobelco","meizu","vivo","buick","tesla","septwolves","samanthathavasa","tomford","jeep","canon","nfl","kiehls","pigeon","zhejiangweishi","snidel","hengyuanxiang","linshimuye","toread","esprit","BASF","gillette","361du","bioderma","UnderArmour","TommyHilfiger","ysl","onitsukatiger","house_of_hello","baidu","robam","konka","jack_wolfskin","office","goldlion","tiantainwuliu","wonderflower","arcteryx","threesquirrels","lego","mindbridge","emblem","grumpycat","bejirog","ccdd","3concepteyes","ferragamo","thermos","Auby","ahc","panasonic","vanguard","FESTO","MCM","lamborghini","laneige","ny","givenchy","zara","jiangshuweishi","daphne","longines","camel","philips","nxp","skf","perfect","toshiba","wodemeilirizhi","Mexican","VANCLEEFARPELS","HARRYPOTTER","mcm","nipponpaint","chenguang","jissbon","versace","girardperregaux","chaumet","columbia","nissan","3M","yuantong","sk2","liangpinpuzi","headshoulder","youngor","teenieweenie","tagheuer","starbucks","pierrecardin","vacheronconstantin","peskoe","playboy","chanel","HarleyDavidson_AE","volvo","be_cheery","mulberry","musenlin","miffy","peacebird","tcl","ironmaiden","skechers","moncler","rimowa","safeguard","baleno","sum37","holikaholika","gucci","theexpendables","dazzle","vatti","nintendo"
  };

  private final List<TrackedRecognition> trackedObjects = new LinkedList<TrackedRecognition>();
  private final Paint boxPaint = new Paint();
  private final BorderedText borderedText;
  private Matrix frameToCanvasMatrix;
  private int frameWidth;
  private int frameHeight;
  private int sensorOrientation;

  public MultiBoxTracker(final Context context) {

    boxPaint.setColor(Color.RED);
    boxPaint.setStyle(Style.STROKE);
    boxPaint.setStrokeWidth(10.0f);
    boxPaint.setStrokeCap(Cap.ROUND);
    boxPaint.setStrokeJoin(Join.ROUND);
    boxPaint.setStrokeMiter(100);

    borderedText = new BorderedText(TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, context.getResources().getDisplayMetrics()));
  }

  public synchronized void setFrameConfiguration(
      final int width, final int height, final int sensorOrientation) {
    frameWidth = width;
    frameHeight = height;
    this.sensorOrientation = sensorOrientation;
  }

  public synchronized void trackResults(final List<Detector.Result> results) {

    processResults(results);
  }

  private Matrix getFrameToCanvasMatrix() {
    return frameToCanvasMatrix;
  }

  public synchronized void draw(final Canvas canvas) {
    final boolean rotated = sensorOrientation % 180 == 90;
    final float multiplier =
        Math.min(
            canvas.getHeight() / (float) (rotated ? frameWidth : frameHeight),
            canvas.getWidth() / (float) (rotated ? frameHeight : frameWidth));
    frameToCanvasMatrix =
        ImageUtils.getTransformationMatrix(
            frameWidth,
            frameHeight,
            (int) (multiplier * (rotated ? frameHeight : frameWidth)),
            (int) (multiplier * (rotated ? frameWidth : frameHeight)),
            sensorOrientation,
            false);
    for (final TrackedRecognition recognition : trackedObjects) {
      final RectF trackedPos = recognition.location.getRectF();

      getFrameToCanvasMatrix().mapRect(trackedPos);
      boxPaint.setColor(recognition.color);

      float cornerSize = Math.min(trackedPos.width(), trackedPos.height()) / 8.0f;
      canvas.drawRoundRect(trackedPos, cornerSize, cornerSize, boxPaint);

      final String labelString =
          !TextUtils.isEmpty(recognition.title)
              ? String.format("%s %.2f", recognition.title, (100 * recognition.detectionConfidence))
              : String.format("%.2f", (100 * recognition.detectionConfidence));

      borderedText.drawText(
          canvas, trackedPos.left + cornerSize, trackedPos.top, labelString + "%", boxPaint);
    }
  }

  private void processResults(final List<Detector.Result> results) {
    final List<Pair<Float, Detector.Result>> rectsToTrack = new LinkedList<Pair<Float, Detector.Result>>();


    for (final Detector.Result result : results) {
      if (result.getBbox() == null) {
        continue;
      }
      rectsToTrack.add(new Pair<Float, Detector.Result>(result.getScore(), result));
    }

    trackedObjects.clear();
    if (rectsToTrack.isEmpty()) {
      return;
    }

    for (final Pair<Float, Detector.Result> potential : rectsToTrack) {
      final TrackedRecognition trackedRecognition = new TrackedRecognition();
      trackedRecognition.detectionConfidence = potential.first;
      trackedRecognition.location = potential.second.getBbox();
      trackedRecognition.title = CLASSNAME[potential.second.getLabel_id()];
      trackedRecognition.color = COLORS[trackedObjects.size()];
      trackedObjects.add(trackedRecognition);

      if (trackedObjects.size() >= COLORS.length) {
        break;
      }
    }
  }

  private static class TrackedRecognition {
    Rect location;
    float detectionConfidence;
    int color;
    String title;
  }
}
